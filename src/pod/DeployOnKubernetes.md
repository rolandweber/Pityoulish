<!--
  This work is released into the Public Domain under the
  terms of the Creative Commons CC0 1.0 Universal license.
  https://creativecommons.org/publicdomain/zero/1.0/
-->

# Run the Server on Kubernetes

I'm using a free Kubernetes cluster in IBM Cloud, which has some restrictions.
If you get it running elsewhere with a different configuration, please let me know in an issue, or provide additional instructions in a PR.

In all cases, it is necessary to customize the deployment for your cluster. The Java RMI exercise requires that the external hostname or IP address of the server is defined as an environment variable in the pod. Only then will Java RMI generate stubs that can actually call the server.
(Unless you want the students to mess around with their `/etc/hosts` configuration, that is.)

I've published a [Pityoulish server image](https://github.com/users/rolandweber/packages/container/pityoulish%2Fserver/45642). However, I'm not planning on keeping it up to date regarding security fixes. Therefore, you should better build and use your own server image. See [Containerfile](./Containerfile) and [Makefile](./Makefile) in this directory for details.



## IBM Cloud

As of 2020-09, a [free Kubernetes cluster](https://cloud.ibm.com/docs/containers?topic=containers-faqs#faq_free) can only expose [NodePort services on the worker node](https://cloud.ibm.com/docs/containers?topic=containers-nodeport), with a non-stable external IP address.
Note that you might get charged for network traffic, even though the cluster is free.

1. Push the server image to the IBM Cloud container registry. (optional)  \
   It is possible to host the image elsewhere, but I don't know whether pulling the image causes billable network traffic.

2. Look up the external IP address of your worker node.
   If your local `kubectl` is configured for your Kubernetes cluster:

   ```bash
   kubectl describe node | grep ExternalIP
   ```

   To configure your local `kubectl` for your Kubernetes cluster, install the [IBM Cloud CLI](https://cloud.ibm.com/docs/containers?topic=containers-cs_cli_install) with the "Kubernetes Service" plugin.
   After logging in with the CLI, lookup the cluster name and retrieve the cluster configuration:
   ```bash
   ibmcloud ks clusters
   ibmcloud ks cluster config -c <cluster-name>
   ```
   If the output of the second command instructs you to set an environment variable or selected a cluster configuration, do it.


3. Edit [ibm-cloud-deploy.yaml](./ibm-cloud-deploy.yaml) as follows:

   - In the `image:` line, specify the location of your server image.

   - After the `name: PITYOULISH_JRMI_HOSTNAME` line, set the value to the external IP address of your worker node.

   The external ports configured in the yaml file are:

   - **`32088`** for the Sockets exercise.
     To use a different port, change the only occurrence.
   - **`31188`** for the registry in the Java RMI exercise.
     To use a different port, change all four occurrences to the same value.
   - `31881` for the exported objects in the Java RMI exercise.
     To use a different port, change all four occurrences to the same value.
   - In the Java RMI exercise, you may use the same port for the registry and the exported objects. To do that, specify only one port for the service "pityoulish-jrmi".

4. Before an exercise, run the server in your cluster:

   ```bash
   kubectl create -f ibm-cloud-deploy.yaml
   ```

5. After an exercise, stop the server in your cluster:

   ```bash
   kubectl delete -f ibm-cloud-deploy.yaml
   ```

### API Key

For convenience, I'm using [an API key](https://medium.com/swlh/api-keys-whats-the-point-8f58d7966f9) when pushing to the container registry, and when logging in with the CLI.

1. [Manage API keys.](https://cloud.ibm.com/docs/account?topic=account-userapikey)

2. When [pushing an image](https://cloud.ibm.com/docs/Registry?topic=Registry-registry_access#registry_access_apikey_auth_other), specify `iamapikey` as username and the API key as password.

3. When logging in with the `ibmcloud` CLI, specify the `--apikey` option.

