#!/usr/bin/groovy

pipeline {
    agent any

    tools {
        maven 'default'
    }

    environment {
        FASIT_ENV = 't0'
        ZONE = 'fss'
        APPLICATION_NAMESPACE = 'default'
        APPLICATION_NAME = 'move-integrasjonspunkt'
    }

    stages {
        stage('Get from nexus') {
            steps {
                script {

                    sh 'curl -o https://beta-meldingsutveksling.difi.no/service/local/repositories/itest/content/no/difi/meldingsutveksling/integrasjonspunkt/1.7.84-SNAPSHOT/integrasjonspunkt-1.7.84-20180409.094710-17.jar'

                }
            }
        }


/*TT
        stage('setup') {
            steps {
                script {
                    sh 'mv move-integrasjonspunkt/* ./'
                    commitHashShort = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    println "${commitHashShort}"
                    pom = readMavenPom file: 'pom.xml'
                    applicationVersion ="${pom.version}-${commitHashShort}.${env.BUILD_ID}"
                    applicationFullName = "${env.APPLICATION_NAME}:${applicationVersion}"

                    dependencyOverrides = '''
                            <dependency>
                                <groupId>no.difi.commons</groupId>
                                <artifactId>commons-asic</artifactId>
                                <version>0.9.3</version>
                            </dependency>
                            <dependency>
                                <groupId>org.postgresql</groupId>
                                <artifactId>postgresql</artifactId>
                                <version>42.1.4</version>
                            </dependency>
                    '''.replaceAll('\n', '\\\\n').replaceAll("\\/", "\\\\/")

                    // Since there we can't access the -MOVE dependency from difis repository we get the one from maven central
                    sh 'sed -i -e s/-MOVE//g dokumentpakking/pom.xml'
                    sh "sed -i -e '0,/RE/s/<dependencies>/<dependencies>${dependencyOverrides}/' pom.xml"
                }
            }
        }

        stage('build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
TT*/

        /*stage('test') {
            steps {
                sh 'mvn verify'
            }
        }*/

        stage('docker build') {
            steps {
                script {
                    docker.withRegistry('https://docker.adeo.no:5000/') {
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
                        sh "nais upload --app ${env.APPLICATION_NAME} -v ${applicationVersion}"
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
            //junit '**/surefire-reports/*.xml'
            archive 'integrasjonspunkt/target/*.jar'
            deleteDir()
            script {
                sh "docker images prune -f"
            }
        }
    }
}
