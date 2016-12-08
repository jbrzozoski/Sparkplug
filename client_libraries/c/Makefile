NAME    :=  sparkplug_b
SNAME   :=  lib$(NAME).a
DNAME   :=  lib$(NAME).so
SRC     :=  $(wildcard src/*.c)
OBJ     :=  $(SRC:.c=.o)
CFLAGS  :=  -Wall -W -Werror -g3 -fPIC -Iinclude/
LDFLAGS :=  -L.
LDLIBS  :=  -l$(...)

.PHONY: all clean fclean re

all: $(SNAME) $(DNAME)

$(SNAME): $(OBJ)
	$(AR) $(ARFLAGS) $@ $^

$(DNAME): LDFLAGS += -shared
$(DNAME): $(OBJ)
	$(CC) $(LDFLAGS) $^ $(LDLIBS) -o $@

clean:
	$(RM) $(OBJ)

fclean: clean
	$(RM) $(SNAME) $(DNAME)

re: fclean all
