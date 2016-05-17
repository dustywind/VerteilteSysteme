
COMPILER = javac
DEBUG = -g
RM = rm -rf

SRCDIR = src
BINDIR = bin

SRCFILES = $(SRCDIR)/vsue/rmi/*.java

all: ; \
    $(COMPILER) $(DEBUG) -sourcepath $(SRCDIR) -d $(BINDIR) $(SRCFILES)

clean: ; \
    $(RM) $(BINDIR)/*
    
