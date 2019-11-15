package com.felstar.ssh.server

import java.nio.file.Paths
import java.util

import org.apache.sshd.common.NamedFactory
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.password.{AcceptAllPasswordAuthenticator, PasswordAuthenticator}
import org.apache.sshd.server.channel.ChannelSession
import org.apache.sshd.server.command.{Command, CommandFactory}
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.scp.ScpCommandFactory
import org.apache.sshd.server.session.ServerSession
import org.apache.sshd.server.shell.{ProcessShellFactory, UnknownCommand}
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory

object ServerMain extends App{

  val sshd = SshServer.setUpDefaultServer()

  sshd.setPort(22)

    // this is so we have same fingerprint each time we are run, so clients don't get spooked
  val hostkey = Paths.get("mykey.pub")
  sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(hostkey))

  //sshd.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE)

   // if you want specific use/name, e.g. ssh user1@localhost
//  sshd.setPasswordAuthenticator((username: String, password: String, session: ServerSession) => {
//    println("authenticating")
//    username=="user1" && password=="password1"
//  })

  sshd.setPasswordAuthenticator(AcceptAllPasswordAuthenticator.INSTANCE)

  // SFTP

  // comment this out if you don't want sftp access
  sshd.setSubsystemFactories(new util.ArrayList[NamedFactory[Command]]() {
    add(new SftpSubsystemFactory.Builder().build)
  })

  // if you want to lock down the file system for the sftp server to a specific directory
  sshd.setFileSystemFactory(new VirtualFileSystemFactory(Paths.get("ftproot").toAbsolutePath))
  // you can even have different paths for different users
  // VirtualFileSystemFactory.setUserHomeDir(userName, userHomeDir)

  // SHELL

  // detects OS, launches shell
  //  sshd.setShellFactory(new InteractiveProcessShellFactory )

  // For Linux
  // sshd.setShellFactory(new ProcessShellFactory("/bin/sh", "-i", "-l"))

  // For Windows
  // sshd.setShellFactory(new ProcessShellFactory("cmd.exe","-c"))

  // for Windows 10 Linux Subsystem
  sshd.setShellFactory(new ProcessShellFactory("bash.exe"))

  // if you don't setShellFactory, there will be no shell access, which you may want

  val factorial = """factorial (\d+)""".r

  val commandFactory: CommandFactory=(_: ChannelSession, command: String) =>
    command match {
      case "woof" => new SimpleCommand {
        def handle = s"Hello, It is ${new java.util.Date()} on this box"
      }
      case "miaow" => (() => s"You said $command"): SimpleCommand // using single abstract method trick
      case factorial(num) => (() => s"You wanted factorial of $num"): SimpleCommand
      case "throw" => {() => throw new Exception("throwing")}: SimpleCommand
      case _ => (() => s"You said $command, I don't know this command"): SimpleCommand
    }

  // SCP

  val factory = new ScpCommandFactory()
  factory.setDelegateCommandFactory(commandFactory)
  sshd.setCommandFactory(factory)

  sshd.start()

  println("Press return to exit ssh server")
  // will wait for a keypress to end
  System.in.read()

  sshd.close()
}
