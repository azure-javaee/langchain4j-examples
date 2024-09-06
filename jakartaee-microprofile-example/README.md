# LangChain4j in Jakarta EE and MicroProfile
This example demonstrates LangChain4J in a Jakarta EE / MicroProfile application on Open Liberty. The application is a chatbot built with LangChain4J and uses Jakarta CDI, Jakarta RESTful Web Services, Jakarta WebSocket, MicroProfile Config, MicroProfile Metrics, and MicroProfile OpenAPI features.

## Prerequisites:

- [Java 17](https://developer.ibm.com/languages/java/semeru-runtimes/downloads)
- Hugging Face API Key
  - Sign up and log in to https://huggingface.co.
  - Go to [Access Tokens](https://huggingface.co/settings/tokens). 
  - Create a new access token with `read` role.
  

## Environment Set Up

To run this example application, navigate  to the `jakartaee-microprofile-example` directory:

```
cd langchain4j-examples/jakartaee-microprofile-example
```

Set the following environment variables:

```
export JAVA_HOME=<your Java 17 home path>
export HUGGING_FACE_API_KEY=<your Hugging Face read token>
```

## Start the application

Use the Maven wrapper to start the application by using the [Liberty dev mode](https://openliberty.io/docs/latest/development-mode.html):

```
./mvnw liberty:dev
```

## Try out the application

- Navigate to http://localhost:9080
- At the prompt, try the following message examples:
  - ```
    What are large language models?
    ```
  - ```
    Which are the most used models?
    ```
  - ```
    any documentation?
    ```


### Try out other models

Navigate to the the [OpenAPI UI](http://localhost:9080/openapi/ui) URL for the following 3 REST APIs:

- [HuggingFaceLanguageModel](https://github.com/langchain4j/langchain4j/blob/main/langchain4j-hugging-face/src/main/java/dev/langchain4j/model/huggingface/HuggingFaceLanguageModel.java)
  - Expand the GET `/api/model/language` API.
    1. Click the **Try it out** button.
    2. Type `When was langchain4j launched?`, or any question, in the question field.
    3. Click the **Execute** button.
  - Alternatively, run the following `curl` command from a command-line session:
    - ```
      curl 'http://localhost:9080/api/model/language?question=When%20was%20langchain4j%20launched%3F'
      ```
- [HuggingFaceChatModel](https://github.com/langchain4j/langchain4j/blob/main/langchain4j-hugging-face/src/main/java/dev/langchain4j/model/huggingface/HuggingFaceChatModel.java)
  - expand the GET `/api/model/chat` API
    1. Click the **Try it out** button.
    2. Type `Which are the most used Large Language Models?`, or any question, in the question field.
    3. Click the **Execute** button.
  - Alternatively, run the following `curl` command from a command-line session:
    - ```
      curl 'http://localhost:9080/api/model/chat?userMessage=Which%20are%20the%20most%20used%20Large%20Language%20models%3F' | jq
      ```
- [InProcessEmbeddingModel](https://github.com/langchain4j/langchain4j-embeddings)
  - expand the GET `/api/model/similarity` API
    1. Click the **Try it out** button.
    2. Type `I like Jakarta EE and MicroProfile.`, or any text, in the the **text1** field.
    3. Type `I like Python language.`, or any text, in the the **text2** field. 
    3. Click the **Execute** button.
  - Alternatively, run the following `curl` command from a command-line session:
    - ```
      curl 'http://localhost:9080/api/model/similarity?text1=I%20like%20Jakarta%20EE%20and%20MicroProfile.&text2=I%20like%20Python%20language.' | jq
      ```


## Running the tests

Because you started Liberty in dev mode, you can run the provided tests by pressing the `enter/return` key from the command-line session where you started dev mode.

If the tests pass, you see a similar output to the following example:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running it.dev.langchan4j.example.ChatServiceIT
[INFO] ...
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.439 s...
[INFO] ...
[INFO] Running it.dev.langchan4j.example.ModelResourceIT
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.733 s...
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```

When you are done checking out the service, exit dev mode by pressing `Ctrl+C` in the command-line session where you ran Liberty, or by typing `q` and then pressing the `enter/return` key.

## Containerize the application

### Prerequisites

* Docker desktop

### Build the docker image

The POM uses the `maven-resources-plugin` with `filtering` to copy the `src/main/docker/Dockerfile` to `target` while substituting the value of the `HUGGING_FACE_API_KEY` environment variable into the `Dockerfile`. The `Dockerfile` is written with the awareness of the file layout relative to its position in the `target` directory. With this design, building the a container image is a matter of starting Docker desktop, `cd target` and running:

```bash
docker buildx build --platform linux/amd64 -t jakartaee-microprofile-example:v1 --pull --file=Dockerfile .
```

## Push to Azure Container Registry

The commands in this section show you how to push the container image to Azure Container Registry for eventually running on AKS. 

### Prerequisites

* An Azure subscription. If you don't have an [Azure subscription](https://learn.microsoft.com/en-us/azure/guides/developer/azure-developer-guide#understanding-accounts-subscriptions-and-billing), create an [Azure free account](https://azure.microsoft.com/free/?ref=microsoft.com&utm_source=microsoft.com&utm_medium=docs&utm_campaign=visualstudio) before you continue.

* Install the [Azure CLI](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli) to run Azure CLI commands.
   * Sign in to the Azure CLI by using the `az login` command. To finish the authentication process, follow the steps displayed in your terminal. For other sign-in options, see [Sign into Azure with Azure CLI](https://learn.microsoft.com/en-us/cli/azure/authenticate-azure-cli#sign-into-azure-with-azure-cli).
   * When you're prompted, install the Azure CLI extension on first use. For more information about extensions, see [Use and manage extensions with the Azure CLI](https://learn.microsoft.com/en-us/cli/azure/azure-cli-extensions-overview).

### Create and sign in to the registry

Execute these commands to create an Azure Container Registry and sign into it.

```bash
export LOCATION=eastus
export UNIQUE_PREFIX=$(date +%s%N | sed 's/.*\([0-9]\{3\}\)N/\1/')
export RESOURCE_GROUP=${UNIQUE_PREFIX}contoso
az group create --name ${RESOURCE_GROUP} --location ${LOCATION}
az acr create --resource-group ${RESOURCE_GROUP} --name ${RESOURCE_GROUP} --admin-enabled true --sku Basic
export LOGIN_SERVER=$(az acr show --resource-group ${RESOURCE_GROUP} --name ${RESOURCE_GROUP} --query 'loginServer' -o tsv)
export USER_NAME=$(az acr credential show --resource-group ${RESOURCE_GROUP} --name ${RESOURCE_GROUP} --query 'username' -o tsv)
export PASSWORD=$(az acr credential show --resource-group ${RESOURCE_GROUP} --name ${RESOURCE_GROUP} --query 'passwords[0].value' -o tsv)
docker login $LOGIN_SERVER -u $USER_NAME -p $PASSWORD
```

### Push the image to Azure Container Registry

Execute these commands to push the image to Azure Container Registry

```bash
docker tag jakartaee-microprofile-example:v1 ${LOGIN_SERVER}/jakartaee-microprofile-example:v1
docker login -u ${USER_NAME} -p ${PASSWORD} ${LOGIN_SERVER}
docker push ${LOGIN_SERVER}/jakartaee-microprofile-example:v1
```

## Deploy Liberty on AKS with the sample app installed

1. Sign in to the Azure Portal at https://aka.ms/publicportal .
1. Ensure you have selected the same subscription in which you created the container registry.
1. Paste this URL into the browser tab in which you have signed into the Azure Portal https://aka.ms/liberty-aks
1. Select **Create**.
1. In the **Resource group** section, select **Create new**. Enter a new name for the resource group.
1. Select the same region in which you have deployed the Azure Container Registry.
1. Select **Next**.
1. In the section **Azure Container Registry (ACR)** select **No**.
1. Use the drop down menu to select the Azure Container Registry you created previously.
1. Select **Next**.
1. Next to **Connect to Azure Application Gateway?** select **Yes**.
1. Select **Next**.
1. Next to **Deploy an application?** select **Yes**.
1. Next to **Application container image path** enter in the value you used for the last argument to `docker tag` previously.  For example `154contoso.azurecr.io/jakartaee-microprofile-example:v1`.
1. Select **Review + create**.
1. Select **Create**.

The deployment takes about 15 minutes.

## Verify the functionality of the app

1. On the screen **Your deployment is complete** select **Outputs** on the left side of the page.
1. Locate the output called **appHttpEndpoint**. Select the copy icon to the right of this output.
1. Paste value this into a new browser tab.
1. When the chat window appears, enter a query, such as "What is MicroProfile?" in the text area and select **Send**.
1. Open up another browser tab, paste the value and append `/metrics?scope=application`. This will show the metrics collected.
