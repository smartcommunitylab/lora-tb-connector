#!/bin/bash
set +x
#ssh key
cat $key > sshkey
chmod 600 sshkey
statusCode=1
APP="lora-tb-connector-prod"
TSTAMP=$(date +%Y.%m.%d-%H.%M.%S)
TSSRV="$TSTAMP $APP:"
RELEASE=$(sed -E -n '/<artifactId>(lora-tb-connector)<\/artifactId>.*/{n;p}' pom.xml | grep -Eo '\d\.\d')
echo $RELEASE
Msg="$TSSRV Build in corso"
URL="https://api.telegram.org/bot${TG_TOKEN}/sendMessage"
CHAT="chat_id=${CHAT_ID}"
curl -s -X POST $URL -d $CHAT -d "text=$Msg"
#curl -s — max-time $TimeLim -d "chat_id=$CHAT_ID&disable_web_page_preview=1&text=$Msg" "https://api.telegram.org/bot$TG_TOKEN/sendMessage"
#ssh -i sshkey -o "StrictHostKeyChecking no" $USR@$IP "sudo service lora-tb-conn stop && /home/$USR/sources/deploy-lora-tb-conn.sh && echo VER=${RELEASE} > /home/dev/lora-tb-connector-env && sudo service lora-tb-conn start "
docker login -u $USERNAME -p $PASSWORD
docker build -t smartcommunitylab/lora-tb-connector:latest --build-arg VER=$RELEASE .
statusCode=$?
if [[ $statusCode -eq 0 ]]; then
  Msg="$TSSRV Immagine Docker creata con successo"
  curl -s -X POST $URL -d $CHAT -d "text=$Msg"
else
  Msg="$TSSRV Immagine Docker creazione errore $?"
  curl -s -X POST $URL -d $CHAT -d "text=$Msg"
fi
rm sshkey
echo $statusCode
exit $statusCode
