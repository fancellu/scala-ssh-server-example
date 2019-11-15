package com.felstar.ssh.server

import java.io.{InputStream, OutputStream}

import org.apache.sshd.server.{Environment, ExitCallback}
import org.apache.sshd.server.channel.ChannelSession
import org.apache.sshd.server.command.Command

trait SimpleCommand extends Command with Runnable{

  var in: InputStream=_
  var out: OutputStream=_
  var err: OutputStream=_
  var callback: ExitCallback=_
  var thread: Thread=_

  override def setInputStream(in: InputStream): Unit = {
    this.in=in
  }

  override def setOutputStream(out: OutputStream): Unit ={
    this.out=out
  }

  override def setErrorStream(err: OutputStream): Unit = {
    this.err=err
  }

  override def setExitCallback(callback: ExitCallback): Unit = {
    this.callback=callback
  }

  def handle(): String

  override def start(channel: ChannelSession, env: Environment): Unit = {

    // Environment.ENV_USER has the logged in user

    thread = new Thread(this, "SimpleCommand")
    thread.setDaemon(true)
    thread.start()
  }

  def writeln(st:String, out: OutputStream): Unit ={
    out.write(s"$st\n".getBytes)
    out.flush()
  }

  def run(): Unit =
    try {
      writeln(handle,out)
      callback.onExit(0)
    } catch {
      case e: Throwable =>
        writeln(e.toString,err)
        callback.onExit(-1)
    }

  override def destroy(channel: ChannelSession): Unit = {
    thread.interrupt()
  }

}
