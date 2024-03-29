name := "scala-ssh-server-example"

version := "0.1"

scalaVersion := "2.13.1"

//libraryDependencies += "org.apache.mina" % "mina-core" % "2.1.3"

mainClass in (Compile, run) := Some("com.felstar.ssh.server.ServerMain")

libraryDependencies += "org.apache.sshd" % "sshd-core" % "2.3.0"
libraryDependencies += "org.apache.sshd" % "sshd-sftp" % "2.3.0"
libraryDependencies += "org.apache.sshd" % "sshd-scp" % "2.3.0"