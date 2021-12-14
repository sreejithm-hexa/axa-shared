# Release Automation - API Rest Call

The ReleaseAutomation object is used to communicate with the [RA API Rest](https://docops.ca.com/ca-release-automation/6-2/en/reference/rest-api-reference) service.

#### Package

`be.axa.deploy.ca.ra.factory.impl`

#### Constructor

`ReleaseAutomation(steps)` : Builds a ReleaseAutomation object.<return>

<code>

    Example :

    def releaseAutomation = new ReleaseAutomation(steps)

#### Methods

**`runDeploymentPlan(DeploymentPlanDTO deploymentPlanDTO)` :** Creates a deployment plan from an existing deployment template. Return the deployment plan Id.


*DeploymentPlanDTO :* Contains the fields used for running a deployment plan

| Parameter                |   Type         | Description
| -------------------------|----------------|--------------------------------------------------------
| deploymentPlan           |   String       | Deployment plan name
| build                    |   String       | Build version name
| project                  |   String       | Project name. A project is created if it does not exist
| deploymentTemplate       |   String       | Deployment template name
| templateCategory         |   String       | Template category name
| application              |   String       | Application name
| deployment               |   String       | Deployment name
| environments             |   List<String> | List of environment names the deployment runs on.
| deploymentStageToPerform |   String       | Execute the stage after deployment has been created. All the stages preceding are executed. **Note :** If [None] is selected, the deployment is only created.
| properties               |   Map          | A map of {name, value} property pairs. If supplied, updates the property values for the deployment plan. **Note:** The property name must exist in the template.

<code>

    Example :

    def properties = ["artifact-url": artifactUrl]

    def deploymentPlanDTO = new DeploymentPlanDTO(
        deploymentPlan:"${version}",
        build: "${version}-${currentBuild.number}",
        project: "${applicationFullName}",
        deploymentTemplate: "SQL default",
        templateCategory:"SQL Deployments",
        application: "SQL",
        deployment: "${deploymentIdentifier}",
        environments: [environment],
        deploymentStageToPerform: "",
        properties: properties
    )

    def deploymentId = releaseAutomation.runDeploymentPlan(deploymentPlanDTO)


**`waitForStatus(String deploymentId, ReleaseAutomationStatus statusToReach)` :**  Run until the status statusToReach is reached.
