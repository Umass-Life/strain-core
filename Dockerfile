
FROM alpine:edge
MAINTAINER nsimsiri
RUN apk add --no-cache openjdk8 curl

# Update aptitude with new repo
RUN apk update

RUN apk add --update bash && rm -rf /var/cache/apk/*

# Install software
RUN apk add --no-cache git
# Make ssh dir
RUN mkdir /root/.ssh/



## Copy over private key, and set permissions
#ADD id_rsa /root/.ssh/id_rsa
#
## Create known_hosts
#RUN touch /root/.ssh/known_hosts
## Add bitbuckets key
#RUN ssh-keyscan bitbucket.org >> /root/.ssh/known_hosts
#
## Clone the conf files into the docker container
#RUN git clone git@bitbucket.org:User/repo.git
