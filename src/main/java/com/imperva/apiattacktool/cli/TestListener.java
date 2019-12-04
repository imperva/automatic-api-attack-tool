package com.imperva.apiattacktool.cli;

import org.testng.ISuite;
import org.testng.ISuiteListener;

public class TestListener implements ISuiteListener {

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("Tool run results output directory = " + suite.getOutputDirectory());
    }

}