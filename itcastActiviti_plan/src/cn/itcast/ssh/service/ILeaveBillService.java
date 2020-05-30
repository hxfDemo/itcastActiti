package cn.itcast.ssh.service;


import cn.itcast.ssh.domain.LeaveBill;

public interface ILeaveBillService {

    LeaveBill findLeaveBillById(Long id);
}
