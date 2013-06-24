

from xml.dom.minidom import parse, Node


manufacturersFile = "../app/res/xml/manufacturers.xml"
canopiesFile = "../app/res/xml/canopies.xml"

manufacturersxmltree = parse(manufacturersFile)
canopiesxmltree = parse(canopiesFile)

manufacturers = dict()
manufacturerurls = dict()
for manufacturerNode in manufacturersxmltree.getElementsByTagName('manufacturer'):
    id = manufacturerNode.getAttribute('id')
    name = manufacturerNode.getAttribute('name')
    url = manufacturerNode.getAttribute('url')
    manufacturers[id] = name
    manufacturerurls[id] = url

print "**** For manufacturers page:\n"
for m in sorted(manufacturers.values()):
    print "* [[" + m + "]]"

print "\n\n\n"

canopies = dict()
canopymanufacturers = dict()
for canopyNode in canopiesxmltree.getElementsByTagName('canopy'):
    id = canopyNode.getAttribute('id')
    name = canopyNode.getAttribute('name')
    cells = canopyNode.getAttribute('cells')
    firstyearofproduction = canopyNode.getAttribute('firstyearofproduction')
    lastyearofproduction = canopyNode.getAttribute('lastyearofproduction')
    manidname = canopyNode.getAttribute('manufacturerid')
    manid = manidname.split()[0]
    manufacturer = manufacturers[manid]
    minsize = canopyNode.getAttribute('minsize')
    maxsize = canopyNode.getAttribute('maxsize')
    url = canopyNode.getAttribute('url')
    canopies[id] =  name
    canopymanufacturers[id] =  manufacturer
    
    print name
    print
    print "__NOTOC__"
    print " Related categories: [[Gear]] [[Canopies]]"
    print
    print "== Type =="
    print
    print "Canopy made by [[" + manufacturer + "]]."
    print
    print "== Description =="
    print
    xb = ""
    if (cells):
        cells = int(cells)
        chambers = cells * 2
        if (cells == 15 or cells == 21 or cells == 27):
            xb = "crossbraced (" + str(cells) + " chamber) "
            chambers = cells
            cells = cells / 3
        print name + " is a " + str(cells) + "-cell " + xb + "canopy."
    print
    if (url):
        print "["+url + "| " + name + " page on " + manufacturer + " website]"
    print "\n\n\n"

    
print "**** For canopies page:\n"
for c in sorted(canopies.values()):
    print "* [[" + c + "]]"


for m in sorted(manufacturers.values()):
    print "\n\n\n"
    print "**** For manufacturer page: " + m + "\n"
    cm = dict()
    for k in canopies.keys():
        if (canopymanufacturers[k] == m):
            cm[k] = canopies[k]
    for c in sorted(cm.values()):
        print "* [[" + c + "]]"



print "\n\n\n"


