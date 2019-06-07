;(function () {

    dataTablesEasy = {};

    dataTablesEasy.config = {
        tableClass: 'datatables-easy'
    };

    dataTablesEasy.init = function () {
        $('table.' + dataTablesEasy.config.tableClass).each(function () {
            return configureDataTable(this)
        });
    };

    function configureDataTable(table) {
        var t = $(table);
        var pageData = getAttributeByPageDataContainer(t);
        if (!pageData) {
            console.log('Attribute with page data is not found on table ' + table);
            return false;
        }
        console.log(pageData);
        var columnDefs = buildColumnDefs(t);
        var orderInfo = initOrders(columnDefs, pageData);

        var totalElements = pageData.totalElements;
        var size = pageData.size;
        var displayStart = pageData.displayStart;
        var displayEnd = pageData.displayEnd;

        var dataTablesSettings = {
            colReorder: true,
            responsive: true,
            pageLength: size,
            pagingType: "full",
            order: [orderInfo.col, orderInfo.dir],
            columnDefs: columnDefs,
            displayStart: displayStart - 1,
            dom: '<"d-flex justify-content-between">t<"row"><"d-flex justify-content-between"<"mt-2"l>ip><"clear">',
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

        renderFitlers(t, columnDefs, pageData);

        //configButtons(dataTablesSettings);

        var appDataTable = t
            .DataTable(dataTablesSettings)
            .on('order.dt', function (e, settings, order) {
                pageData.order = columnDefs[order[0].col].field + '_' + order[0].dir;
                dataTableRequest(this, pageData);
            })
            .on('length.dt', function (e, settings, len) {
                pageData.size = len;
                dataTableRequest(this, pageData);
            })
            .on('page.dt', function () {
                pageData.page = appDataTable.page.info().page + 1;
                dataTableRequest(this, pageData);
            });

        overrideButtonColor();

        appDataTable.columns().every(function (i) {
            $('input', this.footer()).on('keypress', function (event) {
                if (event.keyCode === 13) {
                    subscribeEventAndRequest(this, pageData, columnDefs[i].field, this.value);
                }
            });
        });

        appDataTable.columns().every(function (i) {
            $('select', this.footer()).on('change', function () {
                var value = $.fn.dataTable.util.escapeRegex($(this).val());
                subscribeEventAndRequest(this, pageData, columnDefs[i].field, value);
            });
        });

        new $.fn.dataTable.FixedHeader(appDataTable, {
            headerOffset: 1
        });

        var tableForm = t.closest('form');
        if (!tableForm.length) {
            t.css('border-color', 'red');
            alert('Please wrap highlighted table with form');
        }
        // if (!tableForm.find('input[type="submit"][class="internal-submit"]')) {
        //     $('<input>').attr({
        //         hidden: 'true',
        //         type: 'submit',
        //         class: 'internal-submit'
        //     }).appendTo(tableForm);
        // }
    }

    function initOrders(columnDefs, pageData) {
        var orderColDir = pageData.order;
        var order = {};
        if (orderColDir !== null) {
            var sort = orderColDir.split('_');
            for (var index in columnDefs) {
                var dtInfo = columnDefs[index].field;
                var col = columnDefs[index].targets[0];
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

    function renderFitlers(t, columnDefs, pageData) {
        var thead = t.find('thead');
        var tfoot = t.find('tfoot');
        if (tfoot.length === 0) {
            t.append($('<tfoot></tfoot>'));
            tfoot = t.find('tfoot');
        }
        thead.find('tr').clone(true).appendTo(tfoot);
        tfoot.find('tr>th').each(function (i) {
            var title = $(this).text();
            var field = columnDefs[i].field;
            var type = columnDefs[i].type;
            var enums = getAllEnums();

            if (pageData.filterMap !== null) {
                if (isString(type)) {
                    var filterMap = new Map();
                    filterMap.dict = pageData.filterMap;
                    var searchField = filterMap.get(field);
                    if (searchField !== undefined) {
                        $(this).html('<input type="text" class="form-control" value="' + searchField + '" />');
                    } else {
                        $(this).html('<input type="text" class="form-control" placeholder="Search ' + title + '" />');
                    }
                }
                if (isEnum(type)) {
                    if (enums.hasOwnProperty(field)) {
                        initEnums(this, enums[field]);
                    }
                }
            } else {
                if (isString(type)) {
                    $(this).html('<input type="text" class="form-control" placeholder="Search ' + title + '" />');
                }
                if (isEnum(type)) {
                    if (enums.hasOwnProperty(field)) {
                        initEnums(this, enums[field]);
                    }
                }
            }
        });
    }

    function isString(type) {
        return type === 'string';
    }

    function isEnum(type) {
        return type === 'enum';
    }

    function initEnums(ownerSelect, e) {
        var selectList = document.createElement("select");
        selectList.setAttribute('class', 'form-control');
        for (var val in e) {
            var option = document.createElement("option");
            option.value = e[val];
            option.text = e[val];
            selectList.appendChild(option);
        }
        ownerSelect.innerText = '';
        ownerSelect.appendChild(selectList);
    }

    function subscribeEventAndRequest(ownerEvent, pageData, field, value) {
        var filterMap = new Map();
        if (pageData.filterMap === null) {
            pageData.filterMap = new Map();
        } else {
            filterMap.dict = pageData.filterMap;
        }
        filterMap.put(field, value);
        pageData.filterMap = filterMap.dict;
        dataTableRequest(ownerEvent, pageData);
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
                action: function (e, dt) {
                    var data = dt.buttons.exportData();
                    $.fn.dataTable.fileSave(
                        new Blob([JSON.stringify(data)]),
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
        searchable: true
    };

    function buildColumnDefs(table) {
        var columnDefs = [];
        var columnIndex = 0;
        table.find('thead>tr>th').each(function () {
            var cd = {};
            $.each(this.attributes, function () {
                if (this.specified) {
                    if (this.name.startsWith('dt-')) {
                        cd[this.name.substring(3)] = buildColumnAttributeValue(this.name, this.value);
                    }
                }
            });
            if (!cd.hasOwnProperty('orderable') && cd.field) {
                cd.orderable = true;
            }
            if (!cd.hasOwnProperty('searchable') && cd.field) {
                cd.searchable = true;
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
        if (name.endsWith('-json')) {
            return eval('(' + value + ')');
        }
        return value;
    }

    function dataTableRequest(owner, pageData) {

        if (pageData.page !== null) {
            console.log('page');
            $('<input>').attr({
                type: 'hidden',
                name: 'page',
                value: pageData.page
            }).appendTo(owner);
        }

        if (pageData.size !== null) {
            console.log('size');
            $('<input>').attr({
                type: 'hidden',
                name: 'size',
                value: pageData.size
            }).appendTo(owner);
        }

        if (pageData.order !== null) {
            console.log('order');
            $('<input>').attr({
                type: 'hidden',
                name: 'order',
                value: pageData.order
            }).appendTo(owner);
        }

        if (pageData.filterMap !== null) {
            console.log('filter');
            var filterMap = new Map();
            var filter = 'filter_';
            filterMap.dict = pageData.filterMap;
            var objectKeys = $.map(pageData.filterMap, function(value, key) {return key;});
            for (var key in objectKeys) {
                $('<input>').attr({
                    type: 'hidden',
                    name: filter + objectKeys[key],
                    value: filterMap.get(objectKeys[key])
                }).appendTo(owner);
            }
        }

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

}());
