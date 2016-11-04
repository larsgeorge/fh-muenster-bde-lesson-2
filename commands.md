## Kommandos

ACHTUNG: Bitte die Hinweise aus der `commands.md` aus der ersten Übung beachten, in Bezug auf die Unterschiede zwischen der Cloudera VM, und der Hortonworks Sandbox VM. Für diese Übung kommt hinzu, dass die _Cloudera VM_ eine Eclipse Version unter X-Windows mitbringt, wobei dann alle unten stehenden Befehle direkt in der VM ausgeführt werden. Die _Hortonworks Sandbox_ hat dies nicht, weshalb die Kommandos auf dem Host (Windows, MacOS, oder Linux) mit einer eigenen Eclipse Installation durchzuführen sind. Dazu sind die Befehle unten mit "(ggf. lokalen)" Hinweis entsprechend gekennzeichnet. Alles anderen Befehle muessen dann innerhalb der VM Sitzung ausgeführt werden. Wenn nötig werden zwischen beiden Varianten im Text nochmals genauer unterschieden.

Zuerst kommt das Klonen des Source Repositories in einem (ggf. _lokalen_) Terminal:
```
$ git clone https://github.com/larsgeorge/fh-muenster-bde-lesson-2
```

Download eines Beispiel Textes:
```
$ wget http://www.gutenberg.org/cache/epub/14591/pg14591.txt
```

Testen ob das Dokument da ist:
```
$ less pg14591.txt
```

HDFS anschauen:
```
$ hdfs dfs -ls
$ hdfs dfs -ls /
$ hdfs dfs -ls /user/
$ hdfs dfs -ls /user/cloudera
```
oder (für Hortonworks):
```
$ hdfs dfs -ls /user/root
```

Das Dokument in HDFS speichern und kontrollieren:
```
$ hdfs dfs -put pg14591.txt
$ hdfs dfs -ls /user/cloudera
```
oder
```
$ hdfs dfs -ls /user/root
```
bzw. sollte ein einfaches 
```
$ hdfs dfs -ls
```
reichen, denn das Kommando nimmt immer den aktuellen User Pfad.

Die aktuelle Konfiguration gibt Aufschluss wo sich die eigentlichen Daten innerhalb von HDFS befinden (siehe `dfs.datanode.data.dir`):
```
$ ll /etc/hadoop/conf/
$ cat /etc/hadoop/conf/core-site.xml
```

Kopieren (ggf. lokal) des Source Codes des Projekts in das Eclipse Workspace Verzeichnis. 
Hinweis: Zuerst Eclipse starten und ein neues Projekt anlegen, hier genannt “uebung-2”.
```
$ ll workspace/uebung-2/src/
$ cp -r fh-muenster-bde-lesson-2/src/* workspace/uebung-2/src/
```
Alternativ kann man den Import Wizard von Eclipse nehmen:

![Eclipse Import Dialog](https://github.com/larsgeorge/fh-muenster-bde-lesson-2/raw/master/img/import.png "Eclipse Import Dialog")

Den Wizard auf das Verzeichnis mit der lokalen Repository zeigen lassen ("Select root directory" auf der zweiten Seite des Wizards), dann importieren. Dies kopiert dann alles in die eingestellte Workspace und kann von dort aus benutzt werden.

Nun müssen die JAR Dateien in Eclipse dem Projekt hinzugefügt werden. Dies ist VM abhängig verschieden:

- Für die _Cloudera VM_: Alle JAR Dateien aus /usr/lib/hadoop/client zu dem Projekt hinzufügen.

- Für die _Hortonworks VM_: Über `scp` oder WinSCP müssen alle Dateien aus `/usr/hdp/current/hadoop-client/` und `/usr/hdp/current/hadoop-mapreduce-client/` in ein lokales Verzeichnis kopiert werden. Dann noch die mit kopierte `mapreduce.tar.gz ` entpacken.

Danach alle JAR Dateien über die Projekt Einstellungen mit "Add External JARs" hinzufügen:

![Eclipse Projekt Dialog](https://github.com/larsgeorge/fh-muenster-bde-lesson-2/raw/master/img/jars.png "Eclipse Projekt Dialog")

Jetzt sollte der Code übersetzbar sein, und die IDE keine rot unterstrichenen Zeilen mehr anzeigen.

In Eclipse über "File - Export" (oder einfacher einen Doppelklick auf die `mrjobs.jardesc` im Hauptverzeichnis des Projekts) den Code als JAR zusammen packen (hier `mrjobs.jar` benannt und im Workspace Verzeichnis abgelegt) und die `Driver.class` als Hauptklasse eintragen. Danach kann, für die Cloudera VM, die JAR Datei auf der Kommandozeile ausgeführt werden:
```
$ yarn
$ yarn jar mrjobs.jar
$ yarn jar mrjobs.jar wordcount
$ yarn jar mrjobs.jar wordcount pg14591.txt out1
```

Hinweis: Für die Hortonworks VM muss zuerst die JAR Datei über `scp` oder WinSCP von dem lokalen Verzeichnis in die VM kopiert werden, z. B. so (auf einem MacOS Notebook):
```
$ cd ~/Documents/workspace 
$ ls -la
total 5320
-rw-r--r--   1 larsgeorge  staff    20K Nov  3 13:00 mrjobs.jar

$ scp -P 2222 mrjobs.jar root@localhost: 
root@localhost's password: 
mrjobs.jar                        100%   20KB  19.9KB/s   00:00
```

Ergebnisse ausprobieren (wieder in der VM):
```
$ hdfs dfs -ls
$ hdfs dfs -ls out1/
$ hdfs dfs -cat out1/part-00000 | less
$ hdfs dfs -cat out1/part-00000 | sort -g -k2 -r | less
the 1745
and 848
of 721
to 699
I 607
a 580
in 532
And 426
is 373
The 358
with 293
MEPHISTOPHELES 270
my 262
...
```

Hadoop hat eine Beispiel JAR für MapReduce beigefügt, welche wie folgt, für die Cloudera VM, aufgerufen werden kann:
```
$ yarn jar /usr/lib/hadoop-0.20-mapreduce/hadoop-examples.jar
$ yarn jar /usr/lib/hadoop-0.20-mapreduce/hadoop-examples.jar wordcount pg14591.txt out2
$ hdfs dfs -ls out2/
$ hdfs dfs -cat out2/part-r-00000 | sort -g -k2 -r | less
```

Neben dem WordCount Beispiel hat das Code Repository auch noch den LogGenerator und LogAnalyzer Source dabei:
```
$ yarn jar mrjobs.jar
$ yarn jar mrjobs.jar loggenerator out4/web-large.log 20000000
$ hdfs dfs -ls -h out4/
```

Für die Hortonworks VM muss ein anderes Verzeichnis für die Beispiel JAR genutzt werden:
```  
$ yarn jar /usr/hdp/current/hadoop-mapreduce-client/hadoop-mapreduce-examples.jar 
An example program must be given as the first argument.
Valid program names are:
  aggregatewordcount: An Aggregate based map/reduce program that counts the words in the input files.
  aggregatewordhist: An Aggregate based map/reduce program that computes the histogram of the words in the input files.
  bbp: A map/reduce program that uses Bailey-Borwein-Plouffe to compute exact digits of Pi.
  dbcount: An example job that count the pageview counts from a database.
  distbbp: A map/reduce program that uses a BBP-type formula to compute exact bits of Pi.
  grep: A map/reduce program that counts the matches of a regex in the input.
  join: A job that effects a join over sorted, equally partitioned datasets
  multifilewc: A job that counts words from several files.
  pentomino: A map/reduce tile laying program to find solutions to pentomino problems.
  pi: A map/reduce program that estimates Pi using a quasi-Monte Carlo method.
  randomtextwriter: A map/reduce program that writes 10GB of random textual data per node.
  randomwriter: A map/reduce program that writes 10GB of random data per node.
  secondarysort: An example defining a secondary sort to the reduce.
  sort: A map/reduce program that sorts the data written by the random writer.
  sudoku: A sudoku solver.
  teragen: Generate data for the terasort
  terasort: Run the terasort
  teravalidate: Checking results of terasort
  wordcount: A map/reduce program that counts the words in the input files.
  wordmean: A map/reduce program that counts the average length of the words in the input files.
  wordmedian: A map/reduce program that counts the median length of the words in the input files.
  wordstandarddeviation: A map/reduce program that counts the standard deviation of the length of the words in the input files.
```  
  
Einschub: Mit dem HDFS Kommando kann man nachschauen, wo die einzelnen Blöcke abgelegt werden (hier für die Cloudera VM gezeigt):
```
$ hdfs
$ hdfs fsck
$ hdfs fsck /user/cloudera/out4/web-large.log -files -blocks -locations
14/11/06 06:22:03 WARN ssl.FileBasedKeyStoresFactory: The property 'ssl.client.truststore.location' has not been set, no TrustStore will be loaded
Connecting to namenode via http://quickstart.cloudera:50070
FSCK started by cloudera (auth:SIMPLE) from /127.0.0.1 for path /user/cloudera/out4/web-large.log at Thu Nov 06 06:22:05 PST 2014
/user/cloudera/out4/web-large.log 1561266402 bytes, 12 block(s): OK
0. BP-1256644290-127.0.0.1-1413121757039:blk_1073742414_1594 len=134217728 repl=1 [127.0.0.1:50010]
1. BP-1256644290-127.0.0.1-1413121757039:blk_1073742415_1595 len=134217728 repl=1 [127.0.0.1:50010]
2. BP-1256644290-127.0.0.1-1413121757039:blk_1073742416_1596 len=134217728 repl=1 [127.0.0.1:50010]
3. BP-1256644290-127.0.0.1-1413121757039:blk_1073742417_1597 len=134217728 repl=1 [127.0.0.1:50010]
4. BP-1256644290-127.0.0.1-1413121757039:blk_1073742418_1598 len=134217728 repl=1 [127.0.0.1:50010]
5. BP-1256644290-127.0.0.1-1413121757039:blk_1073742419_1599 len=134217728 repl=1 [127.0.0.1:50010]
6. BP-1256644290-127.0.0.1-1413121757039:blk_1073742420_1600 len=134217728 repl=1 [127.0.0.1:50010]
7. BP-1256644290-127.0.0.1-1413121757039:blk_1073742421_1601 len=134217728 repl=1 [127.0.0.1:50010]
8. BP-1256644290-127.0.0.1-1413121757039:blk_1073742422_1602 len=134217728 repl=1 [127.0.0.1:50010]
9. BP-1256644290-127.0.0.1-1413121757039:blk_1073742424_1604 len=134217728 repl=1 [127.0.0.1:50010]
10. BP-1256644290-127.0.0.1-1413121757039:blk_1073742425_1605 len=134217728 repl=1 [127.0.0.1:50010]
11. BP-1256644290-127.0.0.1-1413121757039:blk_1073742427_1607 len=84871394 repl=1 [127.0.0.1:50010]

Status: HEALTHY
Total size: 1561266402 B
Total dirs: 0
Total files: 1
Total symlinks: 0
Total blocks (validated): 12 (avg. block size 130105533 B)
Minimally replicated blocks: 12 (100.0 %)
Over-replicated blocks: 0 (0.0 %)
Under-replicated blocks: 0 (0.0 %)
Mis-replicated blocks: 0 (0.0 %)
Default replication factor: 1
Average block replication: 1.0
Corrupt blocks: 0
Missing replicas: 0 (0.0 %)
Number of data-nodes: 1
Number of racks: 1
FSCK ended at Thu Nov 06 06:22:05 PST 2014 in 5 milliseconds
The filesystem under path '/user/cloudera/out4/web-large.log' is HEALTHY

$ ll /var/lib/hadoop-hdfs/cache/
$ ll /var/lib/hadoop-hdfs/cache/hdfs/dfs/data/
$ sudo ls -la /var/lib/hadoop-hdfs/cache/hdfs/dfs/data/
$ sudo ls -la -R /var/lib/hadoop-hdfs/cache/hdfs/dfs/data/current
```

Der LogAnalyzer wird wie folgt ausgeführt:
```
$ yarn jar mrjobs.jar loganalyzer out4/web-large.log out4  # produziert Fehler wie erwartet
$ yarn jar mrjobs.jar loganalyzer out4/web-large.log out5

$ hdfs dfs -ls out5/
Found 2 items
-rw-r--r-- 1 cloudera cloudera 0 2014-11-06 04:31 out5/_SUCCESS
-rw-r--r-- 1 cloudera cloudera 70 2014-11-06 04:31 out5/part-00000

$ hdfs dfs -cat out5/part-00000
/contacts.html  1407017280
/index.html     1408035667
/logo.gif       1403731303
```

[ENDE]

### Beispiele
Hinweis: Die echten Verzeichnisnamen sind verschieden, da diese den Blocknamen enthalten und dieser ist Zeit abhängig.
```
[cloudera@quickstart ~]$ sudo ls -la /var/lib/hadoop-hdfs/cache/hdfs/dfs/data/current/BP-1256644290-127.0.0.1-1413121757039/current/finalized/subdir0/subdir2
total 1614748
drwxr-xr-x 2 hdfs hdfs 4096 Nov 6 04:10 .
drwxr-xr-x 5 hdfs hdfs 4096 Nov 1 22:07 ..
-rw-r--r-- 1 hdfs hdfs 1966 Nov 6 00:10 blk_1073742369
-rw-r--r-- 1 hdfs hdfs 23 Nov 6 00:10 blk_1073742369_1549.meta
-rw-r--r-- 1 hdfs hdfs 249321 Nov 6 01:24 blk_1073742372
-rw-r--r-- 1 hdfs hdfs 1955 Nov 6 01:24 blk_1073742372_1552.meta
-rw-r--r-- 1 hdfs hdfs 114734 Nov 6 01:52 blk_1073742380
-rw-r--r-- 1 hdfs hdfs 907 Nov 6 01:52 blk_1073742380_1560.meta
-rw-r--r-- 1 hdfs hdfs 40315 Nov 6 01:52 blk_1073742382
-rw-r--r-- 1 hdfs hdfs 323 Nov 6 01:52 blk_1073742382_1562.meta
-rw-r--r-- 1 hdfs hdfs 101569 Nov 6 01:52 blk_1073742383
-rw-r--r-- 1 hdfs hdfs 803 Nov 6 01:52 blk_1073742383_1563.meta
-rw-r--r-- 1 hdfs hdfs 62569 Nov 6 01:52 blk_1073742384
-rw-r--r-- 1 hdfs hdfs 499 Nov 6 01:52 blk_1073742384_1564.meta
-rw-r--r-- 1 hdfs hdfs 91 Nov 6 04:10 blk_1073742387
-rw-r--r-- 1 hdfs hdfs 11 Nov 6 04:10 blk_1073742387_1567.meta
-rw-r--r-- 1 hdfs hdfs 91 Nov 6 04:10 blk_1073742388
-rw-r--r-- 1 hdfs hdfs 11 Nov 6 04:10 blk_1073742388_1568.meta
-rw-r--r-- 1 hdfs hdfs 114734 Nov 6 03:56 blk_1073742396
-rw-r--r-- 1 hdfs hdfs 907 Nov 6 03:56 blk_1073742396_1576.meta
-rw-r--r-- 1 hdfs hdfs 33909 Nov 6 03:56 blk_1073742398
-rw-r--r-- 1 hdfs hdfs 275 Nov 6 03:56 blk_1073742398_1578.meta
-rw-r--r-- 1 hdfs hdfs 101952 Nov 6 03:56 blk_1073742399
-rw-r--r-- 1 hdfs hdfs 807 Nov 6 03:56 blk_1073742399_1579.meta
-rw-r--r-- 1 hdfs hdfs 46171 Nov 6 03:56 blk_1073742400
-rw-r--r-- 1 hdfs hdfs 371 Nov 6 03:56 blk_1073742400_1580.meta
-rw-r--r-- 1 hdfs hdfs 114734 Nov 6 03:59 blk_1073742408
-rw-r--r-- 1 hdfs hdfs 907 Nov 6 03:59 blk_1073742408_1588.meta
-rw-r--r-- 1 hdfs hdfs 33914 Nov 6 03:59 blk_1073742410
-rw-r--r-- 1 hdfs hdfs 275 Nov 6 03:59 blk_1073742410_1590.meta
-rw-r--r-- 1 hdfs hdfs 101952 Nov 6 03:59 blk_1073742411
-rw-r--r-- 1 hdfs hdfs 807 Nov 6 03:59 blk_1073742411_1591.meta
-rw-r--r-- 1 hdfs hdfs 46172 Nov 6 03:59 blk_1073742412
-rw-r--r-- 1 hdfs hdfs 371 Nov 6 03:59 blk_1073742412_1592.meta
-rw-r--r-- 1 hdfs hdfs 78063027 Nov 6 04:09 blk_1073742413
-rw-r--r-- 1 hdfs hdfs 609875 Nov 6 04:09 blk_1073742413_1593.meta
-rw-r--r-- 1 hdfs hdfs 134217728 Nov 6 04:10 blk_1073742414
-rw-r--r-- 1 hdfs hdfs 1048583 Nov 6 04:10 blk_1073742414_1594.meta
-rw-r--r-- 1 hdfs hdfs 134217728 Nov 6 04:10 blk_1073742415
-rw-r--r-- 1 hdfs hdfs 1048583 Nov 6 04:10 blk_1073742415_1595.meta
-rw-r--r-- 1 hdfs hdfs 134217728 Nov 6 04:10 blk_1073742416
-rw-r--r-- 1 hdfs hdfs 1048583 Nov 6 04:10 blk_1073742416_1596.meta
-rw-r--r-- 1 hdfs hdfs 134217728 Nov 6 04:10 blk_1073742417
-rw-r--r-- 1 hdfs hdfs 1048583 Nov 6 04:10 blk_1073742417_1597.meta
-rw-r--r-- 1 hdfs hdfs 134217728 Nov 6 04:10 blk_1073742418
-rw-r--r-- 1 hdfs hdfs 1048583 Nov 6 04:10 blk_1073742418_1598.meta
-rw-r--r-- 1 hdfs hdfs 134217728 Nov 6 04:10 blk_1073742419
-rw-r--r-- 1 hdfs hdfs 1048583 Nov 6 04:10 blk_1073742419_1599.meta
-rw-r--r-- 1 hdfs hdfs 134217728 Nov 6 04:10 blk_1073742420
-rw-r--r-- 1 hdfs hdfs 1048583 Nov 6 04:10 blk_1073742420_1600.meta
-rw-r--r-- 1 hdfs hdfs 134217728 Nov 6 04:10 blk_1073742421
-rw-r--r-- 1 hdfs hdfs 1048583 Nov 6 04:10 blk_1073742421_1601.meta
-rw-r--r-- 1 hdfs hdfs 134217728 Nov 6 04:10 blk_1073742422
-rw-r--r-- 1 hdfs hdfs 1048583 Nov 6 04:10 blk_1073742422_1602.meta
-rw-r--r-- 1 hdfs hdfs 134217728 Nov 6 04:10 blk_1073742424
-rw-r--r-- 1 hdfs hdfs 1048583 Nov 6 04:10 blk_1073742424_1604.meta
-rw-r--r-- 1 hdfs hdfs 134217728 Nov 6 04:10 blk_1073742425
-rw-r--r-- 1 hdfs hdfs 1048583 Nov 6 04:10 blk_1073742425_1605.meta
-rw-r--r-- 1 hdfs hdfs 84871394 Nov 6 04:10 blk_1073742427
-rw-r--r-- 1 hdfs hdfs 663067 Nov 6 04:10 blk_1073742427_1607.meta

[cloudera@quickstart ~]$ sudo ls -la /var/lib/hadoop-hdfs/cache/hdfs/dfs/data/current/BP-1256644290-127.0.0.1-1413121757039/current/finalized/subdir0/subdir2/blk_1073742414
-rw-r--r-- 1 hdfs hdfs 134217728 Nov 6 04:10 /var/lib/hadoop-hdfs/cache/hdfs/dfs/data/current/BP-1256644290-127.0.0.1-1413121757039/current/finalized/subdir0/subdir2/blk_1073742414

[cloudera@quickstart ~]$ sudo less /var/lib/hadoop-hdfs/cache/hdfs/dfs/data/current/BP-1256644290-127.0.0.1-1413121757039/current/finalized/subdir0/subdir2/blk_1073742414

[cloudera@quickstart ~]$ yarn jar mrjobs.jar loganalyzer out4/web-large.log out4
14/11/06 04:25:27 INFO client.RMProxy: Connecting to ResourceManager at /0.0.0.0:8032
14/11/06 04:25:28 INFO client.RMProxy: Connecting to ResourceManager at /0.0.0.0:8032
org.apache.hadoop.mapred.FileAlreadyExistsException: Output directory hdfs://quickstart.cloudera:8020/user/cloudera/out4 already exists
at org.apache.hadoop.mapred.FileOutputFormat.checkOutputSpecs(FileOutputFormat.java:132)
at org.apache.hadoop.mapreduce.JobSubmitter.checkSpecs(JobSubmitter.java:465)
at org.apache.hadoop.mapreduce.JobSubmitter.submitJobInternal(JobSubmitter.java:343)
at org.apache.hadoop.mapreduce.Job$10.run(Job.java:1295)
at org.apache.hadoop.mapreduce.Job$10.run(Job.java:1292)
at java.security.AccessController.doPrivileged(Native Method)
at javax.security.auth.Subject.doAs(Subject.java:415)
at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1614)
at org.apache.hadoop.mapreduce.Job.submit(Job.java:1292)
at org.apache.hadoop.mapred.JobClient$1.run(JobClient.java:564)
at org.apache.hadoop.mapred.JobClient$1.run(JobClient.java:559)
at java.security.AccessController.doPrivileged(Native Method)
at javax.security.auth.Subject.doAs(Subject.java:415)
at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1614)
at org.apache.hadoop.mapred.JobClient.submitJobInternal(JobClient.java:559)
at org.apache.hadoop.mapred.JobClient.submitJob(JobClient.java:550)
at org.apache.hadoop.mapred.JobClient.runJob(JobClient.java:835)
at org.fhmuenster.bde.mr.LogAnalyzer.run(LogAnalyzer.java:67)
at org.fhmuenster.bde.mr.LogAnalyzer.main(LogAnalyzer.java:73)
at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
at java.lang.reflect.Method.invoke(Method.java:606)
at org.apache.hadoop.util.ProgramDriver$ProgramDescription.invoke(ProgramDriver.java:72)
at org.apache.hadoop.util.ProgramDriver.run(ProgramDriver.java:145)
at org.apache.hadoop.util.ProgramDriver.driver(ProgramDriver.java:153)
at org.fhmuenster.bde.mr.Driver.main(Driver.java:17)
at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
at java.lang.reflect.Method.invoke(Method.java:606)
at org.apache.hadoop.util.RunJar.main(RunJar.java:212)

[cloudera@quickstart ~]$ yarn jar mrjobs.jar loganalyzer out4/web-large.log out5
14/11/06 04:26:13 INFO client.RMProxy: Connecting to ResourceManager at /0.0.0.0:8032
14/11/06 04:26:13 INFO client.RMProxy: Connecting to ResourceManager at /0.0.0.0:8032
14/11/06 04:26:14 WARN mapreduce.JobSubmitter: Hadoop command-line option parsing not performed. Implement the Tool interface and execute your application with ToolRunner to remedy this.
14/11/06 04:26:14 INFO mapred.FileInputFormat: Total input paths to process : 1
14/11/06 04:26:14 INFO mapreduce.JobSubmitter: number of splits:12
14/11/06 04:26:15 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1415257761830_0004
14/11/06 04:26:15 INFO impl.YarnClientImpl: Submitted application application_1415257761830_0004
14/11/06 04:26:15 INFO mapreduce.Job: The url to track the job: http://quickstart.cloudera:8088/proxy/application_1415257761830_0004/
14/11/06 04:26:15 INFO mapreduce.Job: Running job: job_1415257761830_0004
14/11/06 04:26:26 INFO mapreduce.Job: Job job_1415257761830_0004 running in uber mode : false
14/11/06 04:26:26 INFO mapreduce.Job: map 0% reduce 0%
14/11/06 04:27:39 INFO mapreduce.Job: map 1% reduce 0%
14/11/06 04:27:42 INFO mapreduce.Job: map 2% reduce 0%
14/11/06 04:27:46 INFO mapreduce.Job: map 4% reduce 0%
14/11/06 04:27:49 INFO mapreduce.Job: map 5% reduce 0%
14/11/06 04:27:56 INFO mapreduce.Job: map 7% reduce 0%
14/11/06 04:27:59 INFO mapreduce.Job: map 9% reduce 0%
14/11/06 04:28:02 INFO mapreduce.Job: map 12% reduce 0%
14/11/06 04:28:05 INFO mapreduce.Job: map 14% reduce 0%
14/11/06 04:28:10 INFO mapreduce.Job: map 16% reduce 0%
14/11/06 04:28:23 INFO mapreduce.Job: map 17% reduce 0%
14/11/06 04:28:24 INFO mapreduce.Job: map 19% reduce 0%
14/11/06 04:28:31 INFO mapreduce.Job: map 20% reduce 0%
14/11/06 04:28:33 INFO mapreduce.Job: map 23% reduce 0%
14/11/06 04:28:34 INFO mapreduce.Job: map 27% reduce 0%
14/11/06 04:28:36 INFO mapreduce.Job: map 28% reduce 0%
14/11/06 04:28:37 INFO mapreduce.Job: map 29% reduce 0%
14/11/06 04:28:39 INFO mapreduce.Job: map 30% reduce 0%
14/11/06 04:28:40 INFO mapreduce.Job: map 31% reduce 0%
14/11/06 04:28:41 INFO mapreduce.Job: map 32% reduce 0%
14/11/06 04:28:42 INFO mapreduce.Job: map 33% reduce 0%
14/11/06 04:28:44 INFO mapreduce.Job: map 34% reduce 0%
14/11/06 04:28:46 INFO mapreduce.Job: map 35% reduce 0%
14/11/06 04:28:47 INFO mapreduce.Job: map 36% reduce 0%
14/11/06 04:28:59 INFO mapreduce.Job: map 37% reduce 0%
14/11/06 04:29:03 INFO mapreduce.Job: map 39% reduce 0%
14/11/06 04:29:04 INFO mapreduce.Job: map 40% reduce 0%
14/11/06 04:29:06 INFO mapreduce.Job: map 41% reduce 0%
14/11/06 04:29:07 INFO mapreduce.Job: map 42% reduce 0%
14/11/06 04:29:17 INFO mapreduce.Job: map 50% reduce 0%
14/11/06 04:29:26 INFO mapreduce.Job: map 52% reduce 0%
14/11/06 04:29:29 INFO mapreduce.Job: map 55% reduce 0%
14/11/06 04:29:33 INFO mapreduce.Job: map 57% reduce 0%
14/11/06 04:29:36 INFO mapreduce.Job: map 60% reduce 0%
14/11/06 04:29:39 INFO mapreduce.Job: map 63% reduce 0%
14/11/06 04:29:43 INFO mapreduce.Job: map 66% reduce 0%
14/11/06 04:29:46 INFO mapreduce.Job: map 67% reduce 0%
14/11/06 04:29:49 INFO mapreduce.Job: map 69% reduce 0%
14/11/06 04:29:56 INFO mapreduce.Job: map 75% reduce 0%
14/11/06 04:29:59 INFO mapreduce.Job: map 75% reduce 11%
14/11/06 04:30:02 INFO mapreduce.Job: map 76% reduce 14%
14/11/06 04:30:03 INFO mapreduce.Job: map 77% reduce 14%
14/11/06 04:30:06 INFO mapreduce.Job: map 79% reduce 14%
14/11/06 04:30:09 INFO mapreduce.Job: map 82% reduce 14%
14/11/06 04:30:13 INFO mapreduce.Job: map 84% reduce 14%
14/11/06 04:30:16 INFO mapreduce.Job: map 85% reduce 14%
14/11/06 04:30:17 INFO mapreduce.Job: map 86% reduce 14%
14/11/06 04:30:27 INFO mapreduce.Job: map 88% reduce 14%
14/11/06 04:30:28 INFO mapreduce.Job: map 93% reduce 22%
14/11/06 04:30:30 INFO mapreduce.Job: map 95% reduce 22%
14/11/06 04:30:33 INFO mapreduce.Job: map 97% reduce 22%
14/11/06 04:30:36 INFO mapreduce.Job: map 100% reduce 22%
14/11/06 04:30:40 INFO mapreduce.Job: map 100% reduce 31%
14/11/06 04:30:43 INFO mapreduce.Job: map 100% reduce 40%
14/11/06 04:30:46 INFO mapreduce.Job: map 100% reduce 65%
14/11/06 04:30:52 INFO mapreduce.Job: map 100% reduce 67%
14/11/06 04:30:56 INFO mapreduce.Job: map 100% reduce 79%
14/11/06 04:31:02 INFO mapreduce.Job: map 100% reduce 90%
14/11/06 04:31:07 INFO mapreduce.Job: map 100% reduce 100%
14/11/06 04:31:07 INFO mapreduce.Job: Job job_1415257761830_0004 completed successfully
14/11/06 04:31:07 INFO mapreduce.Job: Counters: 50
File System Counters
FILE: Number of bytes read=366667455
FILE: Number of bytes written=734675933
FILE: Number of read operations=0
FILE: Number of large read operations=0
FILE: Number of write operations=0
HDFS: Number of bytes read=1561312850
HDFS: Number of bytes written=70
HDFS: Number of read operations=39
HDFS: Number of large read operations=0
HDFS: Number of write operations=2
Job Counters
Killed map tasks=2
Launched map tasks=14
Launched reduce tasks=1
Data-local map tasks=14
Total time spent by all maps in occupied slots (ms)=1320831
Total time spent by all reduces in occupied slots (ms)=106206
Total time spent by all map tasks (ms)=1320831
Total time spent by all reduce tasks (ms)=106206
Total vcore-seconds taken by all map tasks=1320831
Total vcore-seconds taken by all reduce tasks=106206
Total megabyte-seconds taken by all map tasks=1352530944
Total megabyte-seconds taken by all reduce tasks=108754944
Map-Reduce Framework
Map input records=20000000
Map output records=20000000
Map output bytes=326667431
Map output materialized bytes=366667503
Input split bytes=1392
Combine input records=0
Combine output records=0
Reduce input groups=3
Reduce shuffle bytes=366667503
Reduce input records=20000000
Reduce output records=3
Spilled Records=40000000
Shuffled Maps =12
Failed Shuffles=0
Merged Map outputs=12
GC time elapsed (ms)=15670
CPU time spent (ms)=156070
Physical memory (bytes) snapshot=3446202368
Virtual memory (bytes) snapshot=11459981312
Total committed heap usage (bytes)=2588934144
Shuffle Errors
BAD_ID=0
CONNECTION=0
IO_ERROR=0
WRONG_LENGTH=0
WRONG_MAP=0
WRONG_REDUCE=0
File Input Format Counters
Bytes Read=1561311458
File Output Format Counters
Bytes Written=70

[cloudera@quickstart ~]$ hdfs dfs -ls out5/
Found 2 items
-rw-r--r-- 1 cloudera cloudera 0 2014-11-06 04:31 out5/_SUCCESS
-rw-r--r-- 1 cloudera cloudera 70 2014-11-06 04:31 out5/part-00000
[cloudera@quickstart ~]$ hdfs dfs -cat out5/part-00000
/contacts.html  1407017280
/index.html     1408035667
/logo.gif       1403731303
```
