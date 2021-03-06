#!/usr/bin/env bash

# Helper function for reconfiguring the remote server after a reformat


LOCAL_HOME=/cygdrive/c/Users/M/Desktop/dogue/
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

exe)
    pushd $LOCAL_HOME/dogue/out/artifacts/ && \
    scp dogue_common_jar/dogue-common.jar \
        raven_jar/raven.jar \
        starfire_jar/starfire.jar \
        doge@dogue:dogue_bin
;;
bin)
    FILE=bin.zip
    pushd $LOCAL_HOME/dogue/out/artifacts/ && \
    zip -9 -j $FILE raven_jar/raven.jar starfire_jar/* && \
    scp $FILE doge@dogue:. && \
    rm $FILE && \
    ssh dogue "rm -rf dogue_bin && mkdir -p dogue_bin" && \
    ssh dogue "unzip $FILE -d dogue_bin && rm $FILE && cd dogue_bin && chmod a+r *"
    popd
;;
setup)
    pushd $LOCAL_HOME/scripts && \
    scp setup.sh doge@dogue:. && \
    popd
;;

scripts|s)
    pushd $LOCAL_HOME/scripts && \
    scp * doge@dogue:dogue/scripts && \
    popd
;;

ps)
    if [[ $EUID -eq 0 ]]; then
        echo "This script must not be run as root"
        exit 1
    fi && \
    pushd $REMOTE_HOME && \
    echo 'PS1=\[\e[32m\]at \[\e[37m\]\W/\n\[\e[33m\][\h] \[\e[32m\]\$\[\e[0m\] ' > .bash_history && \
    echo alias s='git status' >> .bash_history && \
    popd
;;

packages)
    if [[ $EUID -ne 0 ]]; then
        echo "This script must be run as root"
        exit 1
    fi && \
    yes Y | apt-get install emacs git openjdk-7-jre openjdk-7-jdk python3 postgresql zip && \
    update-alternatives --install /usr/bin/python python /usr/bin/python3 10 && \
    ln /usr/bin/java /usr/bin/starfire && \
    ln /usr/bin/java /usr/bin/raven
;;

emacs)
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

db)
    if [[ $EUID -ne 0 ]]; then
        echo "This script must be run as root"
        exit 1
    fi && \
    echo "Set password for new user"
    # this doesnt work, just run psql and do the command ALTER USER postgres PASSWORD 'whatever' ;
    # /etc/postgresql/9.1/main/pg_hba.conf
    sudo -u postgres dropdb testdb &> /dev/null
    sudo -u postgres dropuser starfire &> /dev/null
    sudo -u postgres createuser --password --createdb --createrole --superuser starfire && \
    sudo -u postgres createdb testdb -O starfire
;;

repo)
    if [[ $EUID -eq 0 ]]; then
        echo "This script must not be run as root"
        exit 1
    fi && \
    rm -rf dogue && \
    git clone https://github.com/deweyvm/dogue.git dogue
;;

daemon|d)
    if [[ $EUID -ne 0 ]]; then
        echo "This script must be run as root"
        exit 1
    fi && \
    pushd $REMOTE_HOME/dogue/ && \
    register_daemon starfire && \
    register_daemon raven && \
    popd
;;

*)
    echo "Unknown option $1"
    echo "Options:"
    for i in exe \
             bin \
             setup \
             "scripts|s" \
             ps \
             packages \
             emacs \
             db \
             repo \
             "daemon|d" ; do
        echo "    $i"
    done
    exit 1
;;
esac

