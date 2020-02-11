#export EXPOSED_PORT=44242

echo "ECS_CONTAINER_METADATA_FILE: $ECS_CONTAINER_METADATA_FILE"

# Wait until container metadata is complete 
until grep "HostPort" $ECS_CONTAINER_METADATA_FILE > /dev/null; do sleep 1; done
 
cat $ECS_CONTAINER_METADATA_FILE

export EXPOSED_PORT=$(cat $ECS_CONTAINER_METADATA_FILE | awk -v k="HostPort" '{n=split($0,a,","); for (i=1; i<=n; i++) { where = match(a[i], /\"HostPort\"/); if(where) {print a[i]} }  }' | cut -d ":" -f2 | xargs)

echo "Starting Application..."
echo "Port number: $EXPOSED_PORT"
java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$SPRING_ACTIVE_PROFILE -jar /app.war
