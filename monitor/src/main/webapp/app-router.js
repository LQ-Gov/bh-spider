import React, { Component } from "react"
import { render } from "react-dom"
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'



import Index from "./src/Index"
import Dashboard from "./src/dashboard/Dashboard"
import Module from "./src/module/Module"




render(
    <Router>
        <Index>
            <Switch>
                <Route exact path="/" component={Dashboard} />
                <Route path="/dashboard" component={Dashboard} />
                <Route path="/module" component={Module} />
            </Switch>
        </Index>
    </Router>
    , document.getElementById("root"));




//export default Redirect;