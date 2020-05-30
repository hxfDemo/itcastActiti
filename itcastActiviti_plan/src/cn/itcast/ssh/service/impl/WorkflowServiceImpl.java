package cn.itcast.ssh.service.impl;

import cn.itcast.ssh.domain.LeaveBill;
import cn.itcast.ssh.web.form.WorkflowBean;
import com.sun.xml.internal.bind.v2.model.core.ID;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;

import cn.itcast.ssh.dao.ILeaveBillDao;
import cn.itcast.ssh.service.IWorkflowService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowServiceImpl implements IWorkflowService {
    /**
     * 请假申请Dao
     */
    private ILeaveBillDao leaveBillDao;

    private RepositoryService repositoryService;

    private RuntimeService runtimeService;

    private TaskService taskService;

    private FormService formService;

    private HistoryService historyService;

    public void setLeaveBillDao(ILeaveBillDao leaveBillDao) {
        this.leaveBillDao = leaveBillDao;
    }

    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public void setFormService(FormService formService) {
        this.formService = formService;
    }

    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @Override
    public LeaveBill findLeaveBillByTaskId(String taskId) {
        //1：使用任务ID，查询任务对象Task
        Task task = taskService.createTaskQuery()//
                .taskId(taskId)//使用任务ID查询
                .singleResult();
        //2：使用任务对象Task获取流程实例ID
        String processInstanceId = task.getProcessInstanceId();
        //3：使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
                .processInstanceId(processInstanceId)//使用流程实例ID查询
                .singleResult();
        //4：使用流程实例对象获取BUSINESS_KEY
        String buniness_key = pi.getBusinessKey();
        //5：获取BUSINESS_KEY对应的主键ID，使用主键ID，查询请假单对象（LeaveBill.1）
        String id = "";
        if (StringUtils.isNotBlank(buniness_key)) {
            //截取字符串，取buniness_key小数点的第2个值
            id = buniness_key.split("\\.")[1];
        }
        //查询请假单对象
        //使用hql语句：from LeaveBill o where o.id=1
        LeaveBill leaveBill = leaveBillDao.findLeaveBillById(Long.parseLong(id));
        return leaveBill;
    }

    /**
     * 二：已知任务ID，查询ProcessDefinitionEntiy对象，从而获取当前任务完成之后的连线名称，并放置到List<String>集合中
     */
    @Override
    public List<String> findOutComeListByTaskId(String taskId) {
        //返回存放连线的名称集合
        List<String> list = new ArrayList<String>();
        //1:使用任务ID，查询任务对象
        Task task = taskService.createTaskQuery()//
                .taskId(taskId)//使用任务ID查询
                .singleResult();
        //2：获取流程定义ID
        String processDefinitionId = task.getProcessDefinitionId();
        //3：查询ProcessDefinitionEntiy对象
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        //使用任务对象Task获取流程实例ID
        String processInstanceId = task.getProcessInstanceId();
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
                .processInstanceId(processInstanceId)//使用流程实例ID查询
                .singleResult();
        //获取当前活动的id
        String activityId = pi.getActivityId();
        //4：获取当前的活动
        ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
        //5：获取当前活动完成之后连线的名称
        List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
        if (pvmList != null && pvmList.size() > 0) {
            for (PvmTransition pvm : pvmList) {
                String name = (String) pvm.getProperty("name");
                if (StringUtils.isNotBlank(name)) {
                    list.add(name);
                } else {
                    list.add("默认提交");
                }
            }
        }
        return list;
    }

    public List<Comment> findCommentByTaskId(String taskId) {
        List<Comment> list = new ArrayList<Comment>();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();
        List<HistoricTaskInstance> htilist = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
//        if (htilist != null && htilist.size() > 0) {
//            for (HistoricTaskInstance historicTaskInstance : htilist) {
//
//                String id = historicTaskInstance.getId();
//                List<Comment> tasklist = taskService.getTaskComments(id);
//                list.addAll(tasklist);
//            }
//        }
        list = taskService.getProcessInstanceComments(processInstanceId);//同上
        return list;
    }

    @Override
    public void saveSubmitTask(WorkflowBean workflowBean) {
        //获取任务ID
        String taskId = workflowBean.getTaskId();
        //获取连线的名称
        String outcome = workflowBean.getOutcome();
        //批注信息
        String message = workflowBean.getComment();
        //获取请假单ID
        Long id = workflowBean.getId();

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        String processInstanceId = task.getProcessInstanceId();
        Map<String, Object> variables = new HashMap<String, Object>();

        if (outcome != null && outcome.equals("默认提交")) {
            variables.put("outcome", outcome);

        }
        taskService.complete(taskId, variables);

        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (pi == null) {
            LeaveBill bill = leaveBillDao.findLeaveBillById(id);
            bill.setState(2);
        }
    }

    @Override
    public List<Comment> findCommentByLeaveBillId(Long id) {

        LeaveBill leaveBill = leaveBillDao.findLeaveBillById(id);
        String name = leaveBill.getClass().getSimpleName();
        String objId = name + '.' + id;

//        HistoricTaskInstance hpi = historyService.createHistoricTaskInstanceQuery().processInstanceBusinessKey(objId).singleResult();
//        String processInstanceId = hpi.getId();
        HistoricVariableInstance his = historyService.createHistoricVariableInstanceQuery().variableValueEquals("objId", objId).singleResult();
        String processInstanceId = his.getId();
        List<Comment> list = taskService.getProcessInstanceComments(processInstanceId);
        return list;
    }

    @Override
    public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processDefinitionId = task.getProcessDefinitionId();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();

        return processDefinition;
    }

    @Override
    public Map<String, Object> findCoordingByTask(String taskId) {
        Map<String, Object> map = new HashMap<>();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processDefinitionId = task.getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processDefinitionId).singleResult();
        String activityId = pi.getActivityId();
        ActivityImpl activityimpl = processDefinitionEntity.findActivity(activityId);
        map.put("x", activityimpl.getX());
        map.put("y", activityimpl.getY());
        map.put("width", activityimpl.getWidth());
        map.put("height", activityimpl.getHeight());
        return map;
    }

}
