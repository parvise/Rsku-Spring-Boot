/**
 * C4LoaderPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package com.hp.c4.rsku.rSku.security.server.util.icost;

public interface C4LoaderPortType extends java.rmi.Remote {
    public int setHeader(java.lang.String aFileName, Cheader anHeader) throws java.rmi.RemoteException;
    public java.lang.String setHeaderTemp(java.lang.String aDirectory, java.lang.String aFileNamePrefix, java.lang.String aSegment, Cheader anHeader) throws java.rmi.RemoteException;
    public int setOpExpFullData(java.lang.String aFileName, CopExpCost[] aCostList, boolean isLast) throws java.rmi.RemoteException;
    public int setOpExpPartialData(java.lang.String aFileName, CopExpCost[] aCostList, boolean isLast) throws java.rmi.RemoteException;
    public int setCosFullData(java.lang.String aFileName, CcosCost[] aCostList, boolean isLast) throws java.rmi.RemoteException;
    public int setCosPartialData(java.lang.String aFileName, CcosCost[] aCostList, boolean isLast) throws java.rmi.RemoteException;
}
