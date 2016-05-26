##########################################################
#
#从monitor转成mp3并上传，同时移动到backup目录
# 启动方式：
# nohup /usr/local/bin/convert_mp3_upload_s3.sh >/dev/null 2>&1 &
#########################################################

#!/bin/bash
backup_path=/var/nfs/voices/backup
not_upload_nfs_mp3_path=/var/nfs/voice/not_upload_mp3
mp3_path=/var/local/voices/mp3
log_path=/var/log/cti-link/convert_mp3_upload_s3
monitor_path=/var/local/voices/monitor

if [ ! -d $log_path ]; then
    mkdir -p $log_path
fi
if [ ! -d $monitor_path ]; then
    mkdir -p $monitor_path
    chmod 775 $monitor_path
fi
mkdir -p ${backup_path}
mkdir -p ${not_upload_nfs_mp3_path}
mkdir -p ${mp3_path}

convert_mp3_upload_s3() {
    for monitor_file in $(ls ${monitor_path} | head -n 100)
    do
        day=`cat ${monitor_path}/${monitor_file}|grep day|awk -F '=' '{print $2}'`
        wav_path=`cat ${monitor_path}/${monitor_file}|grep wav_path|awk -F '=' '{print $2}'`
        enterprise_id=`cat ${monitor_path}/${monitor_file}|grep enterprise_id|awk -F '=' '{print $2}'`
        wav_file=`cat ${monitor_path}/${monitor_file}|grep wav_file|awk -F '=' '{print $2}'`
        ratio=`cat ${monitor_path}/${monitor_file}|grep ratio|awk -F '=' '{print $2}'`
        aws_s3_path=`cat ${monitor_path}/${monitor_file}|grep aws_s3_path|awk -F '=' '{print $2}'`
        type=`cat ${monitor_path}/${monitor_file}|grep type|awk -F '=' '{print $2}'`
        file_name=${monitor_file}

        echo "`date +%Y-%m-%d@%H:%M:%S` - ${file_name} ${day} ${path} ${enterprise_id} ${wav_file} ${ratio} ${type} ${aws_s3_path}"
        if [ -f ${wav_path}/${wav_file} ]; then 

            result=`/usr/local/bin/lame -b ${ratio} ${wav_path}/${wav_file} ${mp3_path}/${file_name}.mp3 2>&1`
            if [ $? -eq 0 ]; then
                echo "lame result:$?"
                if test -f ${mp3_path}/${file_name}.mp3 ; then
                    aws s3 cp ${mp3_path}/${file_name}.mp3 s3://${aws_s3_path}/${day}/${type}/${enterprise_id}/${file_name}.mp3
                    if [ $? -eq 0 ]; then
                        echo "s3 cp result:$?" 
                        rm -f ${mp3_path}/${file_name}.mp3
                    else
                        echo "upload to s3 failed file=${mp3_path}${file_name}.mp3" 
                        mv ${mp3_path}/${file_name}.mp3 ${not_upload_nfs_mp3_path}/${file_name}.mp3
                    fi
                    rm -f ${wav_path}/${wav_file}
                else
                    mv ${wav_path}/${wav_file} ${backup_path}/${wav_file}
                    echo "file=${mp3_path}/${file_name}.mp3 not exist" 
                fi
            else
                echo "failed converting ${wav_path}/${wav_file} backup to ${backup_path}/${wav_file}" 
                mv ${wav_path}/${wav_file} ${backup_path}/${wav_file}
            fi
        else
            echo "${wav_path}/${wav_file} not exist" 
        fi
        rm -f ${monitor_path}/${monitor_file}
    done
}


while :
do
    convert_mp3_upload_s3 >> ${log_path}/convert_mp3_upload_s3.$(date +%Y%m%d).log
    sleep 0.5
done

