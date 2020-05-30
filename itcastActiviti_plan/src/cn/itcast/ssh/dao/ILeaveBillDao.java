package cn.itcast.ssh.dao;


import cn.itcast.ssh.domain.LeaveBill;

public interface ILeaveBillDao {


    LeaveBill findLeaveBillById(long parseLong);
}
