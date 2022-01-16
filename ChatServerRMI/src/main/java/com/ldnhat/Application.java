package com.ldnhat;

import com.ldnhat.remote.Server;
import com.ldnhat.remote.ServerImpl;
import com.ldnhat.util.Constant;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Application {

    public static void main(String[] args) throws RemoteException {
        Server server = new ServerImpl();
        System.out.println("Server is successfully registred !");

        LocateRegistry.createRegistry(Constant.SERVER_PORT);

        Registry registry = LocateRegistry.getRegistry(Constant.SERVER_PORT);

        try {
            registry.bind("chat", server);

        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

        System.out.println("Server is ready.. waiting for clients!");
    }
}
