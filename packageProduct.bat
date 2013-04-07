set MAVEN_OPTS= -Xms128m -Xmx512m
mvn clean package -Pproduct -Dmaven.test.skip=true && pause