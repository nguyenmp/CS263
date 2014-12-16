#!/usr/bin/env python
#-*- coding: utf-8 -*-

import subprocess
import json
import requests
import time

def sendmessage(message):
    subprocess.Popen(['notify-send', message])
    return

old_list = requests.get('https://astral-casing-728.appspot.com/api/recent_users').json();
while (True):
	new_list = requests.get('https://astral-casing-728.appspot.com/api/recent_users').json();

	fresh_logins = filter(lambda a: a not in old_list, new_list);
	for fresh_login in fresh_logins:
		sendmessage(fresh_login + " has logged into a CSIL machine.")

	old_list = new_list;
	time.sleep(30);
