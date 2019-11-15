package com.felstar.ssh.client

import org.apache.sshd.client.SshClient
import org.apache.sshd.client.keyverifier.KnownHostsServerKeyVerifier

// https://github.com/apache/mina-sshd/blob/master/docs/client-setup.md

object SshCommand extends App{

  val client = SshClient.setUpDefaultClient()

  client.start()

  val clientSession=client.connect("user","localhost",22).verify(2000).getSession

  clientSession.addPasswordIdentity("password")

  clientSession.auth.verify(2000)

  // will throw a RemoteException if anything written to stderr or non standard return code
  val out=clientSession.executeRemoteCommand(args.mkString(" "))
  println(s"out=$out")

  client.stop()
}
