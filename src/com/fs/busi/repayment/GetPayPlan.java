/**
 * Copyright(C),2018-2018, FngS科技有限责任公司
 * Author:Fshuai
 * Date:2018/11/21 0021 下午 23:44
 * Description:生产还款计划
 */
package com.fs.busi.repayment;

import com.fs.busi.BusiProcess;
import com.fs.constants.BusiEnum;
import com.fs.constants.repayment.FeeEnum;
import com.fs.constants.repayment.JihuaParam;
import com.fs.generate.target.entity.YizhiFkxxObj;
import com.fs.generate.target.entity.YizhiHkjihuaObj;
import com.fs.util.common.CommUtil;
import com.fs.util.db.DataBase;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fs.util.date.DateUtil.getNextMonth;

public class GetPayPlan {

	private int firstDays;
	private String payedWyjRiqi;
    private YizhiHkjihuaObj lastHkjh;
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
		firstDays=1;//首期天数
		payedWyjRiqi="";

			/*//669订单
			JihuaParam param=get669Plan(fkxx.getHxfs());
			lstHkjh=getHkRiqi2(fkxx, lstHkjh, param);*/

		//506 568订单
		JihuaParam param=new JihuaParam();
		Map<FeeEnum, Integer> feeFSMap=param.feeFsMap;

		feeFSMap.put(FeeEnum.fwfFee, Integer.parseInt(fkxx.getFysqfs()));
		feeFSMap.put(FeeEnum.qdfFee, Integer.parseInt(fkxx.getQdffsqfs()));

		param.jiesFs=Integer.parseInt(fkxx.getJiesfs());
		param.jixiFs=Integer.parseInt(fkxx.getHxfs());
		param.kouxiFs=Integer.parseInt(fkxx.getKouxifs());
		try {
			param.specialPro=Integer.parseInt(fkxx.getSpeclpro());
		} catch (Exception e) {
			// 防止特殊操作标志为空或字符不符，否则默认为0
		}

		switch (param.specialPro) {
			case JihuaParam.specialPro1:case JihuaParam.specialPro2:
				if (param.kouxiFs==JihuaParam.kouxiFs2) {
					lstHkjh=getHkRiqi4(fkxx, param);
				}else if(param.kouxiFs==JihuaParam.kouxiFs1){
					lstHkjh=getHkRiqi5(fkxx, param);
				}
				break;

			default:
				if (param.kouxiFs==JihuaParam.kouxiFs2) {
					lstHkjh=getHkRiqi1(fkxx, lstHkjh, param);
				}else if(param.kouxiFs==JihuaParam.kouxiFs1){
					lstHkjh=getHkRiqi2(fkxx, lstHkjh, param);
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
						hkjihua.setYhkriqi(getNextMonth(lastHkjh.getKsriqi(), 1));
						hkjihua.setJsriqi(getNextMonth(lastHkjh.getKsriqi(), 1));
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
