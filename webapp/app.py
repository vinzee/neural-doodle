import os
from flask import Flask, request, redirect, url_for
from flask import send_from_directory
import subprocess
import time

UPLOAD_FOLDER = '/media/vipin/SSD/Grad_study/Spring 2018/Mobile Computing/FinalProject/resources/uploads'
RESULT_FOLDER = '/media/vipin/SSD/Grad_study/Spring 2018/Mobile Computing/FinalProject/webapp'
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg'])

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['RESULT_FOLDER'] = RESULT_FOLDER
app.add_url_rule('/uploads/<filename>', 'uploaded_file', build_only=True)

@app.route('/index')
def index():
    return 'Hello world'

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
            flash('No file part')
            return redirect(request.url)
        file = request.files['file']
        # if user does not select file, browser also
        # submit a empty part without filename
        if file.filename == '':
            flash('No selected file')
            return redirect(request.url)
        if file and allowed_file(file.filename):
            # filename = secure_filename(file.filename)
            filename = 'test.jpg'
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            p1 = subprocess.Popen(["python ../../project/fast-neural-doodle/get_mask_hdf5.py --n_colors=4 --style_image=../../project/fast-neural-doodle/data/Renoir/style.png --style_mask=../../project/fast-neural-doodle/data/Renoir/style_mask.png --target_mask=../../project/fast-neural-doodle/data/Renoir/target_mask.png"], shell=True)
            p2 = subprocess.Popen(["th ../../project/fast-neural-doodle/fast_neural_doodle.lua -masks_hdf5 ../../project/fast-neural-doodle/masks.hdf5"], shell=True)
            p1.wait()
            p2.wait()
            if not os.path.exists('/media/vipin/SSD/Grad_study/Spring 2018/Mobile Computing/FinalProject/webapp/out.png'):
                print 'Waiting for results'
                time.sleep(10)
            filename='out.png'
            return redirect(url_for('uploaded_file', filename=filename))
    return '''
    <!doctype html>
    <title>Upload new File</title>
    <h1>Upload new File</h1>
    <form method=post enctype=multipart/form-data>
      <p><input type=file name=file>
         <input type=submit value=Upload>
    </form>
    '''


@app.route('/uploads/<filename>')
def uploaded_file(filename):
    print 'Result folder: ' + str(app.config['RESULT_FOLDER'])
    return send_from_directory(app.config['RESULT_FOLDER'], filename)


@app.route('/doodle')
def doodle():
    return 'Checking doodle API!!'

if __name__ == '__main__':
#    app.run(debug=True, host='0.0.0.0')
    app.run(host='130.85.94.233')
