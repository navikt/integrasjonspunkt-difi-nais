#!/usr/bin/groovy

pipeline {
    agent any

    environment {
        APPLICATION_NAME = 'integrasjonspunkt'
        FASIT_ENVIRONMENT = 't0'
        ZONE = 'fss'
        DOCKER_SLUG = 'integrasjon'
        EXTERNAL_APP_VERSION = '1.7.85-SNAPSHOT'
        EXTERNAL_APP_BUILD_ID = '1.7.85-20180426.094411-8'
    }
    stages {
        stage('initialize') {
            steps {
                init action: 'default'
                init action: 'updateStatus'
            }
        }
        stage('Get from nexus') {
            steps {
                script {
                    env.APPLICATION_VERSION = ${env.EXTERNAL_APP_BUILD_ID}${env.BUILD_ID}
                    sh "curl -o integrasjonspunkt.jar  https://beta-meldingsutveksling.difi.no/service/local/repositories/itest/content/no/difi/meldingsutveksling/${env.APPLICATION_NAME}/${env.EXTERNAL_APP_VERSION}/${env.APPLICATION_NAME}-${env.EXTERNAL_APP_BUILD_ID}.jar"
                }
            }
        }

        stage('docker build') {
            steps {
                dockerUtils action: 'createPushImage'
            }
        }

        stage('validate and upload nais.yaml to m2internal') {
            steps {
                nais action: 'validate'
                nais action: 'upload'
            }
        }

        stage('deploy to nais through jira') {
            steps {
                deploy action: 'jiraPreprod'
            }
        }
    }
    post {
        always {
            postProcess action: 'always'
        }
        success {
            postProcess action: 'success'
        }
        failure {
            postProcess action: 'failure'
        }
    }
}
