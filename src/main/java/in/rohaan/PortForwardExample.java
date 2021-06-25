package in.rohaan;

import io.fabric8.kubernetes.api.model.ConfigBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.LocalPortForward;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PortForwardExample {

    private static final Logger logger = Logger.getLogger(PortForwardExample.class.getSimpleName());

    public static void main(String[] args) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            String namespace = "default";
            logger.log(Level.INFO, "Using namespace: " + namespace);
            Pod pod = new PodBuilder()
                    .withNewMetadata().withGenerateName("test-portforward").endMetadata()
                    .withNewSpec()
                    .addNewContainer()
                    .withName("webseite")
                    .withImage("nginx")
                    .addNewPort()
                    .withContainerPort(80)
                    .endPort()
                    .endContainer()
                    .endSpec()
                    .build();
            Pod createdPod = client.pods().inNamespace(namespace).create(pod);
            String podName = createdPod.getMetadata().getName();
            logger.log(Level.INFO, String.format("Pod %s created", podName));

            int containerPort =  pod.getSpec().getContainers().get(0).getPorts().get(0).getContainerPort();
            client.pods().inNamespace(namespace).withName(podName).waitUntilReady(10, TimeUnit.SECONDS);

            InetAddress inetAddress = InetAddress.getByAddress(new byte[]{0, 0,0,0});
            LocalPortForward portForward = client.pods().inNamespace("default").withName(podName).portForward(/*inetAddress, */containerPort, 8080);
            logger.log(Level.INFO, String.format("Pod %s Port forwarded for 60 seconds at http://127.0.0.1:%s", podName, portForward.getLocalPort()));

            logger.log(Level.INFO, "Checking forwarded port:-");
            final ResponseBody responseBody =  new OkHttpClient()
                    .newCall(new Request.Builder().get().url("http://127.0.0.1:" + portForward.getLocalPort()).build()).execute()
                    .body();
            logger.log(Level.INFO, "Response: \n" + (responseBody != null ? responseBody.string() : "[Empty Body]"));
            logger.log(Level.INFO, "Waiting for 60 minutes");
            Thread.sleep(60 * 60 * 1000L);
            logger.info("Closing forwarded port");
            portForward.close();
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE, "Exception occurred: {}" + e.getMessage(), e);
        }
    }

}
