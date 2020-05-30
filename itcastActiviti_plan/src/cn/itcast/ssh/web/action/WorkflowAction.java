package cn.itcast.ssh.web.action;

import cn.itcast.ssh.domain.LeaveBill;
import cn.itcast.ssh.service.ILeaveBillService;
import cn.itcast.ssh.service.IWorkflowService;
import cn.itcast.ssh.utils.ValueContext;
import cn.itcast.ssh.web.form.WorkflowBean;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;

import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class WorkflowAction extends ActionSupport implements ModelDriven<WorkflowBean> {

    private WorkflowBean workflowBean = new WorkflowBean();

    @Override
    public WorkflowBean getModel() {
        return workflowBean;
    }

    private IWorkflowService workflowService;

    private ILeaveBillService leaveBillService;

    public void setLeaveBillService(ILeaveBillService leaveBillService) {
        this.leaveBillService = leaveBillService;
    }

    public void setWorkflowService(IWorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    /**
     * 部署管理首页显示
     *
     * @return
     */
    public String deployHome() {
        return "deployHome";
    }

    /**
     * 发布流程
     *
     * @return
     */
    public String newdeploy() {
        return "list";
    }

    /**
     * 删除部署信息
     */
    public String delDeployment() {
        return "list";
    }

    /**
     * 查看流程图
     */
    public String viewImage() {
        return null;
    }

    // 启动流程
    public String startProcess() {
        return "listTask";
    }


    /**
     * 任务管理首页显示
     *
     * @return
     */
    public String listTask() {
        return "task";
    }

    /**
     * 打开任务表单
     */
    public String viewTaskForm() {
        return "viewTaskForm";
    }

    // 准备表单数据
    public String audit() {
        String taskId = workflowBean.getTaskId();
        LeaveBill leaveBill = workflowService.findLeaveBillByTaskId(taskId);
        ValueContext.putValueStack(leaveBill);
        List<String> outcomeList = workflowService.findOutComeListByTaskId(taskId);
        ValueContext.putValueContext("outcomeList", outcomeList);
        List<Comment> commentList = workflowService.findCommentByTaskId(taskId);
        ValueContext.putValueContext("commentList", commentList);
        return "taskForm";
    }

    /**
     * 提交任务
     */
    public String submitTask() {
        workflowService.saveSubmitTask(workflowBean);
        return "listTask";
    }

    /**
     * 查看当前流程图（查看当前活动节点，并使用红色的框标注）
     */
    public String viewCurrentImage() {
        String taskId = workflowBean.getTaskId();
        ProcessDefinition pd = workflowService.findProcessDefinitionByTaskId(taskId);
        ValueContext.putValueContext("deploymentId", pd.getDeploymentId());
        ValueContext.putValueContext("imageName", pd.getDiagramResourceName());
        Map<String, Object> map = workflowService.findCoordingByTask(taskId);
        ValueContext.putValueContext("acs",map);
        return "image";
    }

    // 查看历史的批注信息
    public String viewHisComment() {
        Long id = workflowBean.getId();
        LeaveBill leaveBill = leaveBillService.findLeaveBillById(id);
        ValueContext.putValueStack(leaveBill);

        List<Comment> commentList = workflowService.findCommentByLeaveBillId(id);
        ValueContext.putValueContext("commentList", commentList);
        return "viewHisComment";
    }
}
