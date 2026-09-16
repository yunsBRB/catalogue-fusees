mooname

Un projet Spring Boot MVC : tu achètes une inscription (un nom, un psuedo ou autre) qui sera déposée sur une pierre lunaire.

structure :

Les controllers appellent les services, les services passent par les repositories, et les vues ne voient que des DTO. Le reste suit le découpage habituel : entities, models, enums, configs, templates, static.

Côté données : une fusée peut servir à plusieurs missions, une mission a une fusée et un astronaute, un utilisateur a un panier, une commande garde le nom et le prix payés.

L'achat se fait d'un bloc : soit la commande, les places et le panier passent ensemble, soit rien. Un verrou en base empêche de vendre deux fois la dernière place.

