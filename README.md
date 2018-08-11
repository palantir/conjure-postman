conjure-postman
------------
_Generate [Postman](https://www.getpostman.com/) [Collections](https://www.getpostman.com/docs/v6/postman/collections/intro_to_collections) for interacting with [Conjure](https://github.com/palantir/conjure) defined APIs._


## Usage

conjure-postman is an executable which conforms to [RFC 002](https://github.com/palantir/conjure/blob/develop/docs/rfc/002-contract-for-conjure-generators.md).

    usage: conjure-postman generate <target> <output> [--apiPath <apiPath>]
           [--productDescription <description>] --productName <name>
           --productVersion <version>
        --apiPath <apiPath>
        --productDescription <description>
        --productName <name>                 product name
        --productVersion <version>           version number of target product

## Generated collections

`conjure-postman` generates collections in the Postman [Collections 2.1.0](https://schema.getpostman.com/json/collection/v2.1.0/collection.json) format.

Collections contain one folder per service, which contain one request per Endpoint Definition. 


### Variables
Requests require the following variables to be set:

| Variable          | Description                          | Example            | 
| ----------------- | ------------------------------------ | ------------------ |
| `{{HOSTNAME}}`    | Hostname (including protocol)        | `https://site.com` |
| `{{PORT}}`        | Port to use for request              | `443`              |
| `{{AUTH_TOKEN}}`  | Bearer token (only token part)       | `eyJhb...`         |
| `{{%s_API_BASE}}` | Path to API base of product          | `/service/api`     |


#### Collection Defaults
* `{{PORT}}` defaults to `443`
* `{{%s_API_BASE}}`
    * `%s` will be the name of the service, uppercased and underscored.
    * When `--apiBase` is provided it will be used as the value of the variable.

Overrides and non-default options should be set in an [Environment](https://www.getpostman.com/docs/v6/postman/environments_and_globals/manage_environments).


### Body Templates
When a request has a conjure bean as a body argument a template will be provided based on the Bean definition.


### Tests
Postman [Tests](https://www.getpostman.com/docs/v6/postman/scripts/test_scripts) will be configured to expect a successful request and a valid JSON body where appropriate.

