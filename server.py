#!/usr/bin/python
# -*- coding: utf-8 -*-
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
    return datetime.datetime.now().strftime("%d%m%y%H%M%S%f")


def msg_time():
    return datetime.datetime.now().strftime("%H:%M.%S")


def msg_date():
    return datetime.datetime.now().strftime("%d.%m.%y")


static_dir = os.path.dirname(os.path.realpath(__file__)) + '/static/'

msgs = []
urls = []

error = """{
    'status':1
}"""

status = 'status'
ok = 0
time = 'time'
date = 'date'
timestamp = 'timestamp'

tp = 'type'

data = 'data'
up_url = '/static/'


@app.route('/upload', methods=['POST'])
def upload():
    try:
        content = request.headers['Content-Type']
        if content.index('image/') == 0:
            ext = str(content).split("image/")[1]
            filename = current_time() + '.' + ext
            with open(static_dir + filename, 'wb') as f:
                f.write(request.data)
                f.close()
            msg = {}
            resp = {}
            dt = {}
            resp[status] = ok
            msg[time] = dt[time] = msg_time()
            msg[date] = dt[date] = msg_date()
            msg[timestamp] = dt[timestamp] = int(current_time())
            msg[tp] = dt[tp] = 2
            msg[data] = dt[data] = up_url + filename
            msgs.append(msg)
            resp[data] = dt
            return jsonify(resp)
        else:
            return "415 Unsupported Media Type"
    except Exception as e:
        print(e)
        return error


@app.route('/new', methods=['POST'])
def new():
    js = request.get_json()
    try:
        msg = {}
        t = int(js[tp])
        msg[tp] = t
        resp = {}
        if t == 1:
            txt = str(js[data])
            dt = {}
            resp[status] = ok
            msg[time] = dt[time] = msg_time()
            msg[date] = dt[date] = msg_date()
            msg[timestamp] = dt[timestamp] = int(current_time())
            msg[tp] = dt[tp] = 1
            msg[data] = dt[data] = txt
            msgs.append(msg)
            resp[data] = dt
            return jsonify(resp)
        else:
            return error
    except Exception as e:
        print(e)
        return error


@app.route('/messages', methods=['GET'])
def messages():
    resp = {data: msgs, status: ok}
    return jsonify(resp)


if __name__ == '__main__':
    print(sys.argv[1])
    app.run(app.run(host='0.0.0.0', port=int(sys.argv[1])))
