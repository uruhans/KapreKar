source config.sh

today=$(date +"%Y-%m-%d")
filename="$today-$format.zip"

resp=$(
  curl --request POST \
 --url "https://api.lokalise.com/api2/projects/{$project_id}/files/download" \
 --header 'content-type: application/json' \
 --header "x-api-token: $api_key" \
 --data "{\"format\":\"$format\",\"original_filenames\":true}"
)

regex='bundle_url":"(.*)"'

if [[ $resp =~ $regex ]] ; then
  mkdir -p $download_dir
  url=$(echo ${BASH_REMATCH[1]} | tr -d '\')
  curl -o $download_dir$filename --url $url
  unzip -o $download_dir$filename -d $i18n_dir
else
  echo 'could not find bundle_url in response'
  echo $resp
fi