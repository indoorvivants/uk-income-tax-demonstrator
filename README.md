# UK Income tax sanity demonstrator

Interactive breakdown of UK income taxes.

<!--toc:start-->
- [UK Income tax sanity demonstrator](#uk-income-tax-sanity-demonstrator)
  - [Pre-requisites](#pre-requisites)
  - [Build for production](#build-for-production)
  - [Development](#development)
<!--toc:end-->

**[Live version](https://indoorvivants.github.io/uk-income-tax-demonstrator/)**

_Scala 3, Scala.js, Laminar, Vite_


![2023-03-22 15 00 25](https://user-images.githubusercontent.com/1052965/226946421-325a93ba-43ab-4e3e-bb6b-3eaabe72d1d5.gif)

## Pre-requisites

- NPM
- SBT (and JVM of course)
- open mind

```
npm install
```

To install JS dependencies

## Build for production

```
npm run build
```

this will produced an optimised version in the `dist` folder

## Development

In one terminal run (and open the URL that Vite will print out)

```
npm run dev
```

In another terminal run 

```
sbt ~fastLinkJS
```

Now whatever you edit, changes will be eventually reflected in the browser
