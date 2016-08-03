#!/usr/bin/python
#-*- coding: utf-8 -*-
# vim:fileencoding=utf-8

import datetime
import os
import sys

from flask import Flask, request, jsonify

app = Flask(__name__, static_url_path='/static')

files = os.path.dirname(os.path.realpath(__file__)) + '/static/'

app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024
app.config['STATIC_FOLDER'] = 'static'


def current_time():
    return datetime.datetime.now().strftime("%H%M%S%f")


def msg_time():
    return datetime.datetime.now().strftime("%H:%M.%S")


def msg_date():
    return datetime.datetime.now().strftime("%d.%m.%y")


static_dir = os.path.dirname(os.path.realpath(__file__)) + '/static/'

msgs = []

urls = []

error = """{
    'status':'error'
}"""

status = 'status'
ok = 'ok'
time = 'time'
date = 'date'
upload_url = 'upload_url'

up_url = '/static/'

upld = '/upload/'


@app.route('/upload/<filename>', methods=['POST'])
def upload(filename):
    try:
        if request.headers['Content-Type'].index('image/') == 0:
            with open(static_dir + filename, 'w+') as f:
                f.write(request.data)
                f.close()
            resp = {}
            msg = {}
            resp[status] = ok
            msg[time] = resp[time] = msg_time()
            msg[date] = resp[date] = msg_date()
            msg['type'] = 2
            msg['data'] = up_url + filename
            msgs.append(msg)
            return jsonify(resp)

        else:
            return "415 Unsupported Media Type ;)"
    except Exception:
        return error


@app.route('/new', methods=['POST'])
def new():
    js = request.get_json()
    try:
        msg = {}
        tp = int(js['type'])
        msg['type'] = tp
        resp = {status: ok}
        if tp == 1:
            text = str(js['text'])
            resp['text'] = msg['text'] = text
            msg[time] = resp[time] = msg_time()
            msg[date] = resp[date] = msg_date()
            msgs.append(msg)
        elif tp == 2:
            imgtype = js['img_type']
            url = upld + current_time() + '.' + imgtype
            resp[upload_url] = url
        return jsonify(resp)
    except Exception as e:
        print (e)
        return error


@app.route('/messages', methods=['GET'])
def messages():
    resp = {}
    resp['data'] = msgs
    resp[status] = ok
    return jsonify(resp)


if __name__ == '__main__':
    print (sys.argv[1])
    app.run(app.run(host='0.0.0.0', port=int(sys.argv[1])))
