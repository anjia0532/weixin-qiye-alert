#!/bin/bash

: <<-'EOF'
Copyright 2016 Xingwang Liao <kuoruan@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
EOF

PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:~/bin
export PATH

clear

cat >&2 <<-'EOF'
##########################################################
# 微信企业号推送消息                                     #
#                                                        #
# 脚本作者: AnJia <anjia0532@gmail.com>                  #
# 作者博客: https://anjia.ml/                            #
# Github: https://github.com/anjia0532/weixin-qiye-alert #
#                                                        #
##########################################################
EOF

usage() {
	cat >&2 <<-EOF

	请使用: $0 [options] <args...>

    options 可选参数为
        -h, --help      查看脚本说明
        -l, level       输出[ALL/TRACE/DEBUG/INFO/WARN/ERROR/OFF]级别的日志

    args 必填参数为
        -j, --jar       可执行jar路径
        -c, --conf      配置文件路径
        -t, --tag       标签名称
        -m, --msg       推送信息
	EOF
	exit 0
}


#参数
opt_level="ERROR"
opt_jar=""
opt_conf=""
opt_tag=""
opt_msg=""
GETOPT_ARGS=`getopt -o l:j:c:t:m:h -al level:,jar:,conf:,tag:,msg:,help -- "$@"`
eval set -- "$GETOPT_ARGS"

while [ -n "$1" ]
do
	case "$1" in
		-l|--level) opt_level=$2; shift 2;;
		-j|--jar) opt_jar=$2; shift 2;;
		-c|--conf) opt_conf=$2; shift 2;;
		-t|--tag) opt_tag=$2; shift 2;;
		-m|--msg) opt_msg=$2; shift 2;;
		--) break ;;
		-h|--help) usage; break ;;
	esac
done


if [[ ! -r $opt_jar || ! -r $opt_conf || -z $opt_tag || -z $opt_msg ]]; then
	usage
fi

/opt/jdk/bin/java -jar $opt_jar $opt_conf $opt_tag $opt_msg $opt_level

exit 0