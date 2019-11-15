# scala-ssh-server-example

## Shows you how to embed a pure jvm ssh server or client

Run

`com.felstar.ssh.server.ServerMain`

You can:

Command: miaow

`ssh localhost miaow`

> You said miaow

exit code will be 0

e.g. `echo $?`

Command: throw

`ssh localhost throw`

> java.lang.Exception: throwing

exit code will be 255/-1

Shell

`ssh localhost`

SFTP

`sftp localhost`

`ls`

> hello.txt

Client side ssh

`com.felstar.ssh.client.SshCommand miaow`

or

`com.felstar.ssh.client.SshCommand throw`