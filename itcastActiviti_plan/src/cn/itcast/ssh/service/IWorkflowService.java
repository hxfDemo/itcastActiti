package cn.itcast.ssh.service;


import cn.itcast.ssh.domain.LeaveBill;
import cn.itcast.ssh.web.form.WorkflowBean;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;

import java.util.List;
import java.util.Map;

public interface IWorkflowService {


    LeaveBill findLeaveBillByTaskId(String taskId);

    List<String> findOutComeListByTaskId(String taskId);

    List<Comment> findCommentByTaskId(String taskId);

    void saveSubmitTask(WorkflowBean workflowBean);

    List<Comment> findCommentByLeaveBillId(Long id);

    ProcessDefinition findProcessDefinitionByTaskId(String taskId);

    Map<String, Object> findCoordingByTask(String taskId);
}
