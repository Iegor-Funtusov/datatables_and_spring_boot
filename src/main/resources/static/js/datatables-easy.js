;(function() {

    dataTablesEasy = {};

    dataTablesEasy.config = {
    		tableClass: 'datatables-easy'
    };

    dataTablesEasy.init = function () {
        $('table.'+dataTablesEasy.config.tableClass).each(function(){return configureDataTable(this)});
    };

    function configureDataTable(table) {
    	var t = $(table);
        var pdContainer = getAttributeByPageDataContainer(t);
        if (!pdContainer) {
        	console.log('Attribute with page data is not found on table '+table);
        	return false;
        }
        var dtInfoList = initColumnProperties(table);
        var orderInfo = initOrders(dtInfoList, pdContainer);

        var totalElements = pdContainer.totalElements;
        var size = pdContainer.size;
        var displayStart = pdContainer.displayStart;
        var displayEnd = pdContainer.displayEnd;

        var dataTablesSettings = {
                colReorder: true,
                responsive: true,
                pageLength: size,
                pagingType: "full",
                order: [orderInfo.col, orderInfo.dir],
                displayStart: displayStart - 1,
                dom: '<"d-flex justify-content-between"Bl>t<"d-flex justify-content-between"ip><"clear">',
                preDrawCallback: function (settings) {
                    settings.oFeatures.bServerSide = "ssp";
                    settings.bDestroying = true;
                    settings.fnDisplayEnd = function () {
                        return displayEnd;
                    };
                    settings.fnRecordsTotal = function () {
                        return totalElements;
                    };
                    settings.fnRecordsDisplay = function () {
                        return totalElements;
                    };
                }
            };
        
        var columnDefs = buildColumnDefs(t);
       	dataTablesSettings['columnDefs'] = columnDefs;

       	renderFitlers(t, columnDefs);

        //configButtons(dataTablesSettings);
        
        var appDataTable = t
            .DataTable(dataTablesSettings)
            .on('order.dt', function (e, settings, order) {
                pdContainer.order = dtInfoList[order[0].col].field + '_' + order[0].dir;
                dataTableRequest(this, pdContainer);
            })
            .on('length.dt', function (e, settings, len) {
                pdContainer.size = len;
                dataTableRequest(this, pdContainer);
            })
            .on('page.dt', function () {
                pdContainer.page = appDataTable.page.info().page + 1;
                dataTableRequest(this, pdContainer);
            });

        overrideButtonColor();

        appDataTable.columns().every(function (i) {
            $('input', this.footer()).on('keypress', function (event) {
                if (event.keyCode === 13) {
                    setColumnValueAndRunDataTableRequest(this, pdContainer, i, this.value);
                }
            });
        });

        appDataTable.columns().every(function (i) {
            $('select', this.footer()).on('change', function () {
                var value = $.fn.dataTable.util.escapeRegex(
                    $(this).val()
                );
                setColumnValueAndRunDataTableRequest(this, pdContainer, i, value);
            });
        });

        new $.fn.dataTable.FixedHeader(appDataTable, {
            headerOffset: 1
        });

        $('table.'+dataTablesEasy.config.tableClass+' tbody').on('click', 'td.details-control', function () {
            var tr = $(this).closest('tr');
            var row = appDataTable.row(tr);

            if (row.child.isShown()) {
                row.child.hide();
                tr.removeClass('shown');
            } else {
                row.child(format(row.data())).show();
                tr.addClass('shown');
            }
        });

        var drops;
        if ((displayEnd - displayStart + 1) < size) {
            drops = 'down';
        } else {
            drops = 'up';
        }

        if (false) {
        	var startPeriod;
        	var endPeriod;
	        appDataTable.columns().every(function (i) {
	            var column = pdContainer.dataTablesInput.columns[i];
	            if (column !== undefined) {
                    var createTime = "createTime";
                    var updateTime = "updateTime";
	                var columnData = pdContainer.dataTablesInput.columns[i].data;
	                if (columnData === createTime || columnData === updateTime) {
	                    var date = pdContainer.dataTablesInput.columns[i].search.value;
	                    date = date.replace(/ /gi, '').split('-');
	                    var period = new Date(date[0]);
	                    startPeriod = moment(period);
	                    period = new Date(date[1]);
	                    endPeriod = moment(period);
	
	                    if (startPeriod !== undefined && endPeriod !== undefined) {
	
	                        var dtTime = document.getElementById(columnData);
	
	                        var daterangepicker = jQuery(dtTime).daterangepicker({
	                            drops: drops,
	                            startDate: startPeriod,
	                            endDate: endPeriod,
	                            alwaysShowCalendars: true,
	                            ranges: initRanges(startPeriod)
	                        }, function (start, end) {
	                            startPeriod = start;
	                            endPeriod = end;
	                        });
	
	                        appDataTable.columns().every(function (i) {
	                            $('input', this.footer()).on('change', function () {
	                                if (daterangepicker !== undefined) {
	                                    setColumnValueAndRunDataTableRequest(this, pdContainer, i, startPeriod + ':' + endPeriod);
	                                }
	                            });
	                        });
	                    }
	                }
	            }
	        });
        }
        
    	var tableForm = t.closest('form');
    	if (!tableForm.length) {
    		t.css('border-color', 'red');
    		alert('Please wrap highlighted table with form');
    		return;
    	}
    	if (!tableForm.find('input[type="submit"][class="internal-submit"]')) {
    	    $('<input>').attr({
    	        hidden: 'true',
    	        type: 'submit',
    	        class: 'internal-submit' 
    	    }).appendTo(tableForm);
    	}        
    }

    function initColumnProperties(table) {
        var dtInfoList = [];

        var td = table.querySelectorAll("thead th");

        for (var i = 0; i < td.length; i++) {
            var dtInfo = {};
            var tdField = td[i].getAttribute('dt-field');
            if (tdField !== null) {
                dtInfo.field = tdField;
                dtInfo.col = i;
            }
            dtInfoList.push(dtInfo);
        }

        return dtInfoList;
    }

    function initOrders(dtInfoList, pdContainer) {
        var orderColDir = pdContainer.order;
        var order = {};
        if (orderColDir !== null) {
            var sort = orderColDir.split('_');
            for (var index in dtInfoList) {
                var dtInfo = dtInfoList[index].field;
                var col = dtInfoList[index].col;
                if (dtInfo === sort[0]) {
                    order.col = col;
                    order.dir = sort[1];
                }
            }
        } else {
            order.col = 0;
            order.dir = "asc";
        }
        return order;
    }
    
    function renderFitlers(t, columnDefs) {
    	var thead = t.find('thead');
    	var tfoot = t.find('tfoot');
    	if (tfoot.length == 0) {
    		t.append($('<tfoot></tfoot>'))
    		tfoot = t.find('tfoot');
    	}
        thead.find('tr').clone(true).appendTo(tfoot);
        tfoot.find('tr>th').each( function (i) {
            var title = $(this).text();
            $(this).html( '<input type="text" class="form-control" placeholder="Search '+title+'" />' );
            $( 'input', this ).on( 'keyup change', function () {
                if ( table.column(i).search() !== this.value ) {
                    table.column(i).search( this.value ).draw();
                }
            });
        });	                   	
    }
    
    function configButtons(dataTablesSettings) {
        dataTablesSettings['buttons'] = [
            'colvis',
            'print',
            'copyHtml5',
            'csvHtml5',
            'excelHtml5',
            'pdfHtml5',
            {
                text: 'JSON',
                action: function ( e, dt ) {
                    var data = dt.buttons.exportData();
                    $.fn.dataTable.fileSave(
                        new Blob( [ JSON.stringify( data ) ] ),
                        'export_data.json'
                    );
                }
            },
            {
                text: 'Clear',
                action: function () {
                    window.location.replace(window.location.href);
                }
            }
        ];                
    }

    function overrideButtonColor() {
        var btns = document.querySelector('div.dt-buttons.btn-group');
        if (btns) {
	        var kbButtons = btns.getElementsByTagName("button");
	        for (var i = 0; i < kbButtons.length; i++) {
	            kbButtons[i].style.backgroundColor = '#f8f9fa';
	            kbButtons[i].style.color = 'black';
	        }
        }
    }

    function getAttributeByPageDataContainer(table) {
        var pageDataJson = table.attr('dt-page');
        if (pageDataJson) {
        	return eval('(' + pageDataJson + ')');
        }
        return null;
    }
    
    var DEFAULT_COLUMN_DEF = {
    	orderable: true,
    	sortable: true
    };

    function buildColumnDefs(table) {
    	var columnDefs = [];
    	var columnIndex = 0;
    	table.find('thead>tr>th').each(function(){
    		var cd = {};
    		$.each(this.attributes, function(){
    			if (this.specified) {
    				if (this.name.startsWith('dt-')) {
    					cd[this.name.substring(3)] = buildColumnAttributeValue(this.name, this.value);
    				}
    			}
    		});
    		if (!cd.hasOwnProperty('orderable') && cd.field) {
   				cd.orderable = true;
    		}
            cd.targets = [columnIndex];
    		columnDefs.push(cd);
    		columnIndex++;
    	});
    	console.log(columnDefs);
    	return columnDefs;
    }
    
    function buildColumnAttributeValue(name, value) {
    	if (value === 'true' || value === 'false') {
    		return eval(value);
    	}
    	if (name.endsWith('-json')){
    		return eval('(' + value + ')');
    	}
    	return value;
    }

    function dataTableRequest(owner, pdContainer) {
        pdContainer = JSON.stringify(pdContainer);

        console.log('dt = ' + pdContainer);

        $('<input>').attr({
            type: 'hidden',
            name: 'pageData',
            value: pdContainer
        }).appendTo(owner);

        var button = document.createElement("button");
        button.setAttribute('type', 'submit');
        button.style.visibility = 'hidden';
        owner.parentElement.appendChild(button);
        jQuery(button).trigger("click");
    }

    function initRanges(start) {
        return {
            'All': [start, moment()],
            'Today': [moment(), moment()],
            'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            'Last 7 Days': [moment().subtract(6, 'days'), moment()],
            'Last 30 Days': [moment().subtract(29, 'days'), moment()],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        };
    }

    function setColumnValue(pdContainer, i, value) {
        pdContainer.dataTablesInput.columns[i].search.value = value;
        pdContainer.dataTablesInput.columns[i].search.regex = true;
        pdContainer.dataTablesInput.columnsAsMap = null;
    }

    function setColumnValueAndRunDataTableRequest(thisVal, pdContainer, i, value) {
        setColumnValue(pdContainer, i, value);
        dataTableRequest(thisVal, pdContainer);
    }

} ());
