#!/usr/bin/groovy

pipeline {
    agent any

    environment {
        FASIT_ENV = 't0'
        ZONE = 'fss'
        APPLICATION_NAMESPACE = 'default'
        APPLICATION_NAME = 'integrasjonspunkt'
        EXTERNAL_APP_VERSION='1.7.84-SNAPSHOT'
        EXTERNAL_APP_BUILD_ID='1.7.84-20180409.094710-17'



    }

    stages {
        stage('Get from nexus') {
            steps {
                script {
                    applicationFullName = "${env.APPLICATION_NAME}:${env.EXTERNAL_APP_BUILD_ID}"
                    sh "curl -o integrasjonspunkt.jar  https://beta-meldingsutveksling.difi.no/service/local/repositories/itest/content/no/difi/meldingsutveksling/${env.APPLICATION_NAME}/${env.EXTERNAL_APP_VERSION}/${env.APPLICATION_NAME}-${env.EXTERNAL_APP_BUILD_ID}.jar"
                }
            }
        }


        stage('docker build') {
            steps {
                script {
                    docker.withRegistry('https://repo.adeo.no:5443/') {
                        def image = docker.build("integrasjon/${applicationFullName}")
                        image.push()
                        image.push 'latest'
                    }
                }
            }
        }

        stage('validate and upload nais.yaml to m2internal') {
            steps {
                script {
                    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'nexus-user', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD']]) {
                        sh 'nais validate'
                        sh "nais upload --app ${env.APPLICATION_NAME} -v ${env.EXTERNAL_APP_BUILD_ID}"
                    }
                }
            }
        }

        stage('deploy to nais through jira') {
            steps {
                script {
                    def postBody = [
                            fields: [
                                    project          : [key: "DEPLOY"],
                                    issuetype        : [id: "14302"],
                                    customfield_14811: [value: "${env.FASIT_ENV}"],
                                    customfield_14812: "${applicationFullName}",
                                    customfield_17410: "${env.BUILD_URL}input/Deploy/",
                                    customfield_19015: [id: "22707", value: "Yes"],
                                    customfield_19413: "${env.APPLICATION_NAMESPACE}",
                                    customfield_19610: [value: "${env.ZONE}"],
                                    summary          : "Automatisk deploy av ${applicationFullName} til ${env.FASIT_ENV}"
                            ]
                    ]

                    def jiraPayload = groovy.json.JsonOutput.toJson(postBody)

                    echo jiraPayload

                    def response = httpRequest([
                            url                   : "https://jira.adeo.no/rest/api/2/issue/",
                            authentication        : "nais-user",
                            consoleLogResponseBody: true,
                            contentType           : "APPLICATION_JSON",
                            httpMode              : "POST",
                            requestBody           : jiraPayload
                    ])

                    def jiraIssueId = readJSON([text: response.content])["key"]
                    currentBuild.description = "Waiting for <a href=\"https://jira.adeo.no/browse/$jiraIssueId\">${jiraIssueId}</a>"
                }
            }
        }

    }

    post {
        always {
            archive '*.jar'
            deleteDir()
            script {
                sh "docker images prune -f"
            }
        }
    }
}
