package ua.cn.stu.plugin.sinoperation;

import ua.cn.stu.plugin.api.Plugin;
import ua.cn.stu.plugin.api.UnaryOperator;

public class SinPlugin implements Plugin, UnaryOperator {

    public static final String PLUGIN_NAME = "Sinus operation plugin";

    @Override
    public double calculateUnary(double operand) {
        return Math.sin(operand);
    }

    @Override
    public void invoke() {
        System.out.println(PLUGIN_NAME + " loaded");
    }

}
