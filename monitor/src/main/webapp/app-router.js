import React, { Component } from "react"
import { render } from "react-dom"
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import { Provider } from 'mobx-react';



import Index from "./src/Index"
import Dashboard from "./src/dashboard/Dashboard"
import Module from "./src/module/Module"
//import UIStore from "./src/store/UIStore"
import ModuleStore from "./src/store/ModuleStore"


const stores = {
    moduleStore: new ModuleStore(),
}


render(
    <Provider stores={stores}>
        <Router>
            <Index>
                <Switch>
                    <Route exact path="/" component={Dashboard} />
                    <Route path="/dashboard" component={Dashboard} />
                    <Route path="/module" component={Module} />
                </Switch>
            </Index>
        </Router>
    </Provider>
    , document.getElementById("root"));




//export default Redirect;