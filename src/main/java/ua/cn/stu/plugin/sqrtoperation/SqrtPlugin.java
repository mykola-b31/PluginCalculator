package ua.cn.stu.plugin.sqrtoperation;

import ua.cn.stu.plugin.api.Plugin;
import ua.cn.stu.plugin.api.UnaryOperator;

public class SqrtPlugin implements Plugin, UnaryOperator {

    public static final String PLUGIN_NAME = "Square root operation plugin";

    @Override
    public double calculateUnary(double operand) {
        return operand * operand;
    }

    @Override
    public void invoke() {
        System.out.println(PLUGIN_NAME + " loaded");
    }

}
