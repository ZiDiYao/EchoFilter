package com.echofilter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)

public class EchoFilterApplication {
    public static void main(String[] args) {
        System.out.println("EchoFilterApplication starts to run");
        System.out.println("             _ooOoo_");
        System.out.println("            o8888888o");
        System.out.println("            88\" . \"88");
        System.out.println("            (| -_- |)");
        System.out.println("            O\\  =  /O");
        System.out.println("         ____/`---'\\____");
        System.out.println("       .'  \\\\|     |//  `.");
        System.out.println("      /  \\\\|||  :  |||//  \\");
        System.out.println("     /  _||||| -:- |||||_  \\");
        System.out.println("     |   | \\\\\\  -  /// |   |");
        System.out.println("     | \\_|  ''\\---/''  |_/ |");
        System.out.println("     \\  .-\\__  `-`  ___/-. /");
        System.out.println("   ___`. .'  /--.--\\  `. . __");
        System.out.println("\"\"\" '<  `.___\\_<|>_/___.'  >'\"\"\"");
        System.out.println("| | :  `- \\`.;`\\ _ /`;.`/ - ` : | |");
        System.out.println("\\  \\ `-.   \\_ __\\ /__ _/   .-` /  /");
        System.out.println("=====`-.____`-.___\\_____/___.-`____.-'======");
        System.out.println("             `=---='");
        System.out.println("");
        System.out.println("...............................................");
        System.out.println("        佛祖保佑       NO BUG");
        System.out.println("...............................................");

        SpringApplication.run(EchoFilterApplication.class, args);

    }

}
