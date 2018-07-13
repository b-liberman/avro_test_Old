node  {

	stage('start') {
		echo "Starting pipeline triggered by git"
	}
	
	stage('clone repository') {
		def scmVars = checkout scm
		echo "building ${scmVars.GIT_BRANCH}"
	}
	
	jarFileName = 'avro_test'
	jarFileVersion = currentBuild.number
	
	stage('build project') {
		dir('Avro Test') {
			echo "./gradlew -PjarFileName=${jarFileName} clean build test"
	    	sh "./gradlew -PjarFileName=${jarFileName} -PjarFileVersion=${jarFileVersion} clean build test"
	    }
	}
	
	stage('build docker image') {
    	dir('Avro Test') {
    		app = docker.build("boris/avrotest:${currentBuild.number}", "--build-arg jarFileName=${jarFileName} --build-arg jarFileVersion=${jarFileVersion} .")
    	}
	}
	
	stage('set tag in GIT') {
    	sh "git tag ${jarFileName}/${jarFileVersion} -m 'automatic jenkins tag 123'"
    	sh "git push origin master --tags"
	}
}