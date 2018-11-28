package com.fs.constants.repayment;

public class JihuaParam {
        /*
         * 末期结束方式
         */
        /**
         * 末期20日到期
         */
        public static final int jiesFs1=1;
        /**
         * 末期对日
         */
        public static final int jiesFs2=2;
        /**
         * 末期对日减一
         */
        public static final int jiesFs3=3;
        /*
         * 还息方式
         */
        /**
         * normal 按利率分期计息,先息后本
         */
        public static final int jixiFs1=1;
        /**
         * 等额还款方式计息
         */
        public static final int jixiFs2=2;
        /**
         * 等额30天区间收息，上扣，20日还30天利息
         */
        public static final int jixiFs21=21;
        /**
         * 一次性收息
         */
        public static final int jixiFs3=3;
        /**
         * 月息年本
         * 利息每月分期
         * 本金按年分期
         */
        public static final int jixiFs4=4;
        /**
         * 本金 第一年15%，第二年10%，第三年75%
         */
        public static final int jixiFs41=41;
        /**
         * 本金 第一年10%，第二年10%，第三年80%
         */
        public static final int jixiFs42=42;
        /**
         * 固定30日计息
         */
//			public static final int jixiFs5=5;
        /*
         * 扣息方式
         */
        /**
         * 下扣息
         */
        public static final int kouxiFs1=1;
        /**
         * 上扣息
         */
        public static final int kouxiFs2=2;
        /*
         * 费用收取方式
         */
        /**
         * 费用一次性收取
         */
        public static final int feeFs1=1;
        /**
         * 费用分期收取
         */
        public static final int feeFs2=2;
        /*
         * 期次区间标志
         */
        /**
         * 首期
         */
        public static final int qiciShouQi=1;
        /**
         * 第零期
         */
        public static final int qiciLingQi=2;
        /**
         * 末期
         */
        public static final int qiciMoQi=4;
        /**
         * 中间期
         */
        public static final int qiciZhongQi=3;
        /**
         * 新增收本期
         */
        public static final int qicijiBen=6;
        /*
         *特殊处理标识
         */
        /**
         * 正常处理
         */
        public static final int specialPro0=0;
        /**
         * 提前还款
         */
        public static final int specialPro1=1;
        /**
         * 早偿
         */
        public static final int specialPro2=2;
        /**
         * 末期利息小数处理
         */
        public static final int specialPro3=3;
        /**
         * 当期多还不抵违约金
         */
        public static final int specialPro4=4;
        /**
         * 末期服务费小数处理
         */
        public static final int specialPro5=5;
        /*
         * 违约金减免方式
         */
        /**
         *违约金正常减免
         */
        public static final int wyjjmzc=1;
        /**
         *违约金正常减免
         */
        public static final int wyjjmgq=2;
}
