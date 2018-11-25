/**
 * Copyright(C),2018-2018, FngS科技有限责任公司
 * Author:Fshuai
 * Date:2018/11/21 0021 下午 23:44
 * Description:生产还款计划
 */
package com.fs.busi.repayment;

import com.fs.busi.BusiProcess;
import com.fs.constants.BusiEnum;
import com.fs.constants.repayment.JihuaParam;
import com.fs.entity.repayment.entity.PayPlanStatic;
import com.fs.entity.repayment.param.PayParam;
import com.fs.generate.target.entity.YizhiFkxxObj;
import com.fs.generate.target.entity.YizhiHkjihuaObj;
import com.fs.util.common.CommUtil;
import com.fs.util.date.DateUtil;
import com.fs.util.db.DataBase;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class BuildPayPlan {

    private  YizhiHkjihuaObj lastHkjh;


	/**
	 * 根据还款方式获得不同还款计划
	 * @param fkxx 放款信息
	 * @return
	 */
	public List<YizhiHkjihuaObj> getPayPlan(YizhiFkxxObj fkxx){
		/*
		 * 计划信息初始化
		 */
		List<YizhiHkjihuaObj> lstHkjh=new ArrayList<>();
		PayPlanStatic.firstDays=1;//首期天数
		PayPlanStatic.payedWyjRiqi="";

			/*//669订单
			JihuaParam param=get669Plan(fkxx.getHxfs());
			lstHkjh=getHkRiqi2(fkxx, lstHkjh, param);*/

		//506 568订单
		PayParam payParam = new PayParam(fkxx);


		switch (payParam.getSpecialPro()) {
			case JihuaParam.specialPro1:case JihuaParam.specialPro2:
				if (payParam.getKouxiFs()==JihuaParam.kouxiFs2) {
//					lstHkjh=getHkRiqi4(fkxx, param);
				}else if(payParam.getKouxiFs()==JihuaParam.kouxiFs1){
//					lstHkjh=getHkRiqi5(fkxx, param);
				}
				break;

			default:
				if (payParam.getKouxiFs()==JihuaParam.kouxiFs2) {
					lstHkjh=new ShangkouPlan(payParam).getPlan(fkxx);
				}else if(payParam.getKouxiFs()==JihuaParam.kouxiFs1){
//					lstHkjh=getHkRiqi2(fkxx, lstHkjh, param);
				}
				break;
		}


		return lstHkjh;
	}
/**
 * 还款计划订单入表
 * @param lstHkjihua
 */


    private void insertPlan(List<YizhiHkjihuaObj> lstHkjihua, YizhiFkxxObj fkxx) {

		lastHkjh = null;
		for (int i = 0; i < lstHkjihua.size(); i++) {
			//初始化
			YizhiHkjihuaObj hkjihua = lstHkjihua.get(i);
			hkjihua.setDqsfjqbz(BusiEnum.NO.getValue());
			String plfzkey = fkxx.getPlfzuhao() + fkxx.getOrderno() + hkjihua.getQici();
			BigInteger bigintKey = BigInteger.valueOf(plfzkey.hashCode());
			if (BusiEnum.YES.getValue().equals(fkxx.getShifcuoq()) ) {

				/*20日放款错期 */
				if (fkxx.getFkrq().substring(6, 8).equals("20")) {
					if (hkjihua.getQici().equals("0")) {
						lastHkjh = hkjihua;
						continue;
					} else if (hkjihua.getQici().equals("1")) {
						hkjihua.setKsriqi(lastHkjh.getKsriqi());
						hkjihua.setYhkriqi(DateUtil.getNextMonth(lastHkjh.getKsriqi(), 1));
						hkjihua.setJsriqi(DateUtil.getNextMonth(lastHkjh.getKsriqi(), 1));
						hkjihua.setYinghklx(CommUtil.Operator.add.add(lastHkjh.getYinghklx(), hkjihua.getYinghklx()).toString());
						hkjihua.setYinghkbj(CommUtil.Operator.add.add(lastHkjh.getYinghkbj(), hkjihua.getYinghkbj()).toString());
						hkjihua.setYhfwfee(CommUtil.Operator.add.add(lastHkjh.getYhfwfee(), hkjihua.getYhfwfee()).toString());
						hkjihua.setYhqdffee(CommUtil.Operator.add.add(lastHkjh.getYhqdffee(), hkjihua.getYhqdffee()).toString());
						hkjihua.setYhfee(CommUtil.Operator.add.add(lastHkjh.getYhfee(), hkjihua.getYhfee()).toString());
						lastHkjh = hkjihua;
					} else {
						hkjihua.setJsriqi(hkjihua.getYhkriqi());
						if (lastHkjh != null) {
							hkjihua.setKsriqi(lastHkjh.getJsriqi());
						}
					}
					hkjihua.setQici(String.valueOf(i - 1));

				} else {
					hkjihua.setJsriqi(hkjihua.getYhkriqi());
					if (lastHkjh != null) {
						hkjihua.setKsriqi(lastHkjh.getJsriqi());
					}
				}

				hkjihua.setJsriqi(hkjihua.getYhkriqi());
				if (lastHkjh != null) {
					hkjihua.setKsriqi(lastHkjh.getJsriqi());
				}
				lastHkjh = hkjihua;
			}
			hkjihua.setPlfzuhao(bigintKey.divideAndRemainder(BigInteger.valueOf(100))[1].toString());
			try {
				DataBase.insert(BusiProcess.statement, hkjihua, "yizhi_hkjihua");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


}
