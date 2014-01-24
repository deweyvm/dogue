#!/usr/bin/env bash

LOCAL_HOME=/cygdrive/c/Users/M/Desktop/whatever/
REMOTE_HOME=/home/doge/
BUILD_NAME=raven
GAME_NAME=starfire

mkchown() {
    mkdir -p $1 && \
    chown $2:$2 $1
}

makeuser() {
    id $1
    if [[ $? -ne 0 ]] ; then
        useradd --no-create-home $1 &> /dev/null
    fi
}

register_daemon() {
    name=$1
    makeuser $name && \
    mkchown /var/log/$name $name && \
    mkchown /var/run/$name $name && \
    cp scripts/$name /etc/init.d/ && \
    chown root:root /etc/init.d/$name && \
    chmod +x /etc/init.d/$name && \
    update-rc.d $name defaults && \
    update-rc.d $name enable
}

case $1 in
local-bin)
    pushd $LOCAL_HOME/whatever/out/artifacts/whatever_server_jar/ &&\
    ssh dogue mkdir -p whatever_bin &&\
    scp * dogue:whatever_bin/ &&\
    popd
;;

local-setup)
    pushd $LOCAL_HOME/scripts && \
    scp setup.sh doge@dogue:. && \
    popd
;;

local-scripts)
    pushd $LOCAL_HOME/scripts && \
    scp * doge@dogue:whatever/scripts && \
    popd
;;

remote-ps)
    if [[ $EUID -eq 0 ]]; then
        echo "This script must not be run as root"
        exit 1
    fi && \
    pushd $REMOTE_HOME && \
    echo 'PS1=\[\e[32m\]at \[\e[37m\]\W/\n\[\e[33m\][\h] \[\e[32m\]\$\[\e[0m\] ' > .bash_history && \
    echo alias s='git status' >> .bash_history && \
    popd
;;

remote-packages)
    if [[ $EUID -ne 0 ]]; then
        echo "This script must be run as root"
        exit 1
    fi && \
    yes Y | apt-get install emacs git openjdk-7-jre openjdk-7-jdk python3 postgresql && \
    sudo update-alternatives --install /usr/bin/python python /usr/bin/python3 10
;;

remote-emacs)
    if [[ $EUID -eq 0 ]]; then
        echo "This script must not be run as root"
        exit 1
    fi && \
    rm -rf .emacs .emacs.d && \
    git clone https://github.com/deweyvm/emacs.git emacs && \
    cp emacs/.emacs . && \
    cp -ra emacs/.emacs.d . && \
    rm -rf emacs
;;

remote-db)
    if [[ $EUID -ne 0 ]]; then
        echo "This script must be run as root"
        exit 1
    fi && \
    echo "Set password for new user" && \
    sudo -u postgres createuser --password --createdb --no-createrole --no-superuser starfire && \
    sudo -u postgres createdb testdb -O starfire
;;
remote-repo)
    if [[ $EUID -eq 0 ]]; then
        echo "This script must not be run as root"
        exit 1
    fi && \
    rm -rf whatever && \
    git clone https://github.com/deweyvm/whatever.git whatever
;;

remote-stuff)
    if [[ $EUID -ne 0 ]]; then
        echo "This script must be run as root"
        exit 1
    fi && \
    pushd $REMOTE_HOME/whatever/ && \
    register_daemon starfire && \
    register_daemon raven && \
    popd
;;

*)
    echo "Unknown option $1"
    exit 1
;;
esac

