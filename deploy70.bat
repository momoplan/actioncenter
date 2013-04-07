set MAVEN_OPTS= -Xms128m -Xmx512m
mvn clean tomcat:deploy -Pprelaunch -Dmaven.test.skip=true && pause