import cv2
import easygui
import imageio
import matplotlib.pyplot as plt
import tkinter as tk
import sys
import os
import numpy as np
from tkinter import filedialog
from tkinter import *
from PIL import ImageTk,Image


top=tk.Tk()
top.geometry('400x400')
top.title('Cartoonify Your Image !')
top.configure(background='white')
label=Label(top,background='#CDCDCD', font=('calibri',20,'bold'))




#fileipenbox helps us import a file and stire the filepath as a string
def upload():
    ImagePath=easygui.fileopenbox()
    cartoonify(ImagePath)


def cartoonify(ImagePath):
    #read Image
    originalImage=cv2.imread(ImagePath)
    originalImage=cv2.cvtColor(originalImage,cv2.COLOR_BGR2RGB)
    #Print(Image)
    #confirm that the image is chosen
    if originalImage is None:
        print("Image not chosen!!!Please chodse the appropriate file")
        sys.exit()



    Resized1=cv2.resize(originalImage,(960,540))
    #plt.imshow(Resized1,camp="gray")    



    grayScaleImage=cv2.cvtColor(originalImage,cv2.COLOR_BGR2GRAY)
    Resized2=cv2.resize(grayScaleImage,(960,540))





    #applying median Blur to smothen the  image
    smoothGrayScale=cv2.medianBlur(grayScaleImage,5)
    Resize3=cv2.resize(smoothGrayScale,(960,540))



    #Retrieving the edges for cartoon effect
    #by using thresholding technique
    getEdge=cv2.adaptiveThreshold(smoothGrayScale,255,
    cv2.ADAPTIVE_THRESH_MEAN_C,cv2.THRESH_BINARY,9,9)
    Resize4=cv2.resize(getEdge,(960,540))


    #applying bilateral filter to remove the noises and 
    #  keep the edges as sharp as possible
    colorImage=cv2.bilateralFilter(originalImage,9,300,300)
    Resize5=cv2.resize(colorImage,(960,540))


    #masking edged image with "BEAUTIFY" image
    cartoonImage=cv2.bitwise_and(colorImage,colorImage,mask=getEdge)
    Resize6=cv2.resize(cartoonImage,(960,540))


    #plotting all transitions together
    images=[Resized1,Resized2,Resize3,Resize4,Resize5,Resize6]
    fig,axes=plt.subplots(3,2,  figsize=(8,8),subplot_kw={'xticks':[],'yticks':[]}, 
    gridspec_kw=dict(hspace=0.1,wspace=0.1))
    for i,ax in enumerate(axes.flat):
        ax.imshow(images[i],cmap='gray')

    save1=Button(top,text="Save cartoon image",command=lambda: save(ReSized6, ImagePath),padx=30,pady=5)
    save1.configure(background='#364156', foreground='white',font=('calibri',10,'bold'))
    save1.pack(side=TOP,pady=50)
    


    plt.show()


    #functionality of save button
def save(Resized6,ImagePath):
    #saving an image using imwrite
    new_name="cartoonified Image"
    path1=os.path.dirname(ImagePath)
    extension=os.path.splitext(ImagePath)[1]
    path=os.path.join(path1,new_name+extension)
    cv2.imwrite(path,cv2.cvtcolor(Resized6,cv2.cv2.COLOR_RGB2BGR))
    I="Image saved by name"+new_name+"at"+path
    tk.messagebox.showinfo(title=none,message=I)



upload=Button(top,text="Cartoonify an Image",command=upload,padx=10,pady=5)
upload.configure(background='#364156', foreground='white',font=('calibri',10,'bold'))
upload.pack(side=TOP,pady=50)

top.mainloop()

