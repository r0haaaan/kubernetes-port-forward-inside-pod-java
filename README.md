# Kubernetes Pod Port Forward in Java using Fabric8 K8s Client inside Pod

This demo project tries to create an nginx pod and port forward it to the pod
from where it got created.

## How to Build?
```
mvn clean install
```

## How to Run?
You would need to have access to some Kubernetes Cluster in order to test this. I used minikube for testing. First compile
the project and then use Eclispe JKube's Kubernetes Maven Plugin to deploy this application to Kubernetes
```
mvn package k8s:build k8s:resource k8s:apply
```

Check created pods after build finishes:
```
port-forward-inside-pod : $ kubectl get pods
NAME                                               READY   STATUS             RESTARTS   AGE
k8s-port-forward-inside-pod-ccd8949b9-stkvn        1/1     Running            0          9s
```
We're port forward and requesting in this small program. Check application logs to see whether request to forwarded port was successful or not:
```
port-forward-inside-pod : $ kubectl logs pod/k8s-port-forward-inside-pod-ccd8949b9-stkvn
Starting the Java application using /opt/jboss/container/java/run/run-java.sh ...
INFO exec  java -javaagent:/usr/share/java/jolokia-jvm-agent/jolokia-jvm.jar=config=/opt/jboss/container/jolokia/etc/jolokia.properties -javaagent:/usr/share/java/prometheus-jmx-exporter/jmx_prometheus_javaagent.jar=9779:/opt/jboss/container/prometheus/etc/jmx-exporter-config.yaml -XX:+UseParallelOldGC -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=20 -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -XX:MaxMetaspaceSize=100m -XX:+ExitOnOutOfMemoryError -cp "." -jar /deployments/k8s-port-forward-inside-pod-1.0-SNAPSHOT-jar-with-dependencies.jar  
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by org.jolokia.util.ClassUtil (file:/usr/share/java/jolokia-jvm-agent/jolokia-jvm.jar) to constructor sun.security.x509.X500Name(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String)
WARNING: Please consider reporting this to the maintainers of org.jolokia.util.ClassUtil
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Jun 25, 2021 10:09:41 AM in.rohaan.PortForwardExample main
INFO: Using namespace: default
I> No access restrictor found, access to any MBean is allowed
Jolokia: Agent started with URL https://172.17.0.30:8778/jolokia/
Jun 25, 2021 10:09:42 AM in.rohaan.PortForwardExample main
INFO: Pod test-portforward2kcfm created
Jun 25, 2021 10:09:46 AM in.rohaan.PortForwardExample main
INFO: Pod test-portforward2kcfm Port forwarded for 60 seconds at http://127.0.0.1:9001
Jun 25, 2021 10:09:46 AM in.rohaan.PortForwardExample main
INFO: Checking forwarded port:-
Jun 25, 2021 10:09:46 AM in.rohaan.PortForwardExample main
INFO: Response: 
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>

Jun 25, 2021 10:09:46 AM in.rohaan.PortForwardExample main
INFO: Waiting for 60 minutes
```

## Cleanup
Once you're done testing, undeploy application:
```
mvn k8s:undeploy
```
