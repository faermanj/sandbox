TIMESTAMP=$( date "+%Y%m%d%H%M%S" )

aws cloudformation create-stack \
  --stack-name "hello-ec2-$TIMESTAMP" \
  --template-body "file://hello-ec2.cfn.json"
