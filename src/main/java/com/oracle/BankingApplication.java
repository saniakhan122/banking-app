package com.oracle;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

import com.oracle.controller.AccountCreationRequestRestController;
import com.oracle.controller.AdminRestController;
import com.oracle.controller.BankingRestController;
import com.oracle.controller.CustomerLoginRestController;
import com.oracle.controller.CustomerRestController;

@ApplicationPath("/api")  // base URI for all REST endpoints
public class BankingApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();

        // Register all your REST controllers explicitly
        resources.add(AccountCreationRequestRestController.class);
        resources.add(AdminRestController.class);
        resources.add(BankingRestController.class);
//        resources.add(BankingViewController.class);
        resources.add(CustomerLoginRestController.class);
        resources.add(CustomerRestController.class);

        return resources;
    }
}
