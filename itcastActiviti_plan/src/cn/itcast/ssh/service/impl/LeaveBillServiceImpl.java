package cn.itcast.ssh.service.impl;

import cn.itcast.ssh.dao.ILeaveBillDao;
import cn.itcast.ssh.domain.LeaveBill;
import cn.itcast.ssh.service.ILeaveBillService;

public class LeaveBillServiceImpl implements ILeaveBillService {

	private ILeaveBillDao leaveBillDao;

	public void setLeaveBillDao(ILeaveBillDao leaveBillDao) {
		this.leaveBillDao = leaveBillDao;
	}

	@Override
	public LeaveBill findLeaveBillById(Long id) {
		LeaveBill bill = leaveBillDao.findLeaveBillById(id);
		return bill;
	}
}
