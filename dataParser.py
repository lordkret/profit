from bs4 import BeautifulSoup
import urllib2                                       

def get_page():
    html = urllib2.urlopen("http://www.euro-millions.com/results-archive-2015") 
    return html

def get_stories(content):
    soup = BeautifulSoup(content)
    turnifier = 0
    titles_html = ""

    for div in soup.body.findAll("li", { "class": ["ball", "lucky-star"]}):
        if (turnifier < 6): 
            titles_html +=  div.get_text() + ","
            turnifier += 1
        else:
            titles_html += div.get_text() + "\n"
            turnifier = 0

    return titles_html

print get_stories(get_page())
