package org.omg.CosNaming.NamingContextExtPackage;


/**
* org/omg/CosNaming/NamingContextExtPackage/InvalidAddress.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from c:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8u60/4407/corba/src/share/classes/org/omg/CosNaming/nameservice.idl
* Tuesday, August 4, 2015 11:07:52 AM PDT
*/

public final class InvalidAddress extends org.omg.CORBA.UserException
{

  public InvalidAddress ()
  {
    super(InvalidAddressHelper.id());
  } // ctor


  public InvalidAddress (String $reason)
  {
    super(InvalidAddressHelper.id() + "  " + $reason);
  } // ctor

} // class InvalidAddress
